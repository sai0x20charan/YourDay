package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.model.PlatformSurfaceModel
import com.charan.yourday.data.repository.A2uiRepository
import androidx.a2ui.compose.runtime.A2uiMessageParser
import androidx.a2ui.compose.ui.A2uiCatalog
import androidx.a2ui.compose.ui.A2uiMessageProcessor
import androidx.a2ui.model.catalog.functions.A2uiLocaleProvider
import androidx.a2ui.model.catalog.functions.A2uiMessageFormatter
import androidx.a2ui.model.catalog.functions.A2uiUrlOpener
import androidx.a2ui.model.processor.A2uiMessageParser as IA2uiMessageParser
import androidx.a2ui.model.processor.A2uiMessageProcessor as IA2uiMessageProcessor
import androidx.a2ui.model.processor.A2uiSurfaceModel
import androidx.a2ui.model.processor.processInput
import androidx.compose.material3.a2ui.catalog.A2uiAudioPlayerRenderer
import androidx.compose.material3.a2ui.catalog.A2uiImageRenderer
import androidx.compose.material3.a2ui.catalog.A2uiVideoRenderer
import androidx.compose.material3.a2ui.catalog.MaterialA2uiBasicCatalogV1Defaults
import androidx.compose.material3.a2ui.catalog.materialA2uiBasicCatalogV1
import java.text.MessageFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class A2uiRepositoryImpl : A2uiRepository {

    private val basicCatalog: A2uiCatalog = materialA2uiBasicCatalogV1(
        image = MaterialA2uiBasicCatalogV1Defaults.image(A2uiImageRenderer { _, _, _, _, _ -> }),
        video = MaterialA2uiBasicCatalogV1Defaults.video(A2uiVideoRenderer { _, _, _ -> }),
        audioPlayer = MaterialA2uiBasicCatalogV1Defaults.audioPlayer(A2uiAudioPlayerRenderer { _, _, _, _ -> }),
        urlOpener = A2uiUrlOpener { _ -> },
        messageFormatter = A2uiMessageFormatter { pattern, locale, args ->
            MessageFormat(pattern, locale).format(args)
        },
        localeProvider = A2uiLocaleProvider { Locale.getDefault() }
    )

    private val catalogV091: A2uiCatalog = A2uiCatalog(
        catalogId = "https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json",
        components = basicCatalog.components.toList(),
        functions = basicCatalog.functions.toList(),
        themeSchema = basicCatalog.themeSchema
    )

    private val catalogV09: A2uiCatalog = A2uiCatalog(
        catalogId = "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json",
        components = basicCatalog.components.toList(),
        functions = basicCatalog.functions.toList(),
        themeSchema = basicCatalog.themeSchema
    )

    private val aliasCatalogV09: A2uiCatalog = A2uiCatalog(
        catalogId = "https://a2ui.org/specification/v0_9/basic_catalog.json",
        components = basicCatalog.components.toList(),
        functions = basicCatalog.functions.toList(),
        themeSchema = basicCatalog.themeSchema
    )

    private val aliasCatalogV091: A2uiCatalog = A2uiCatalog(
        catalogId = "https://a2ui.org/specification/v0_9_1/basic_catalog.json",
        components = basicCatalog.components.toList(),
        functions = basicCatalog.functions.toList(),
        themeSchema = basicCatalog.themeSchema
    )

    private val parser: IA2uiMessageParser<String> = A2uiMessageParser()

    private val processor: IA2uiMessageProcessor = A2uiMessageProcessor(
        catalogs = listOf(basicCatalog, catalogV091, catalogV09, aliasCatalogV09, aliasCatalogV091)
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var isSurfaceCreated = false

    init {
        scope.launch {
            processor.collectMessages()
        }
        scope.launch {
            processor.outboundEvents.collect { event ->
                println("A2UI outbound event: $event")
            }
        }
    }

    override val surfaces: StateFlow<List<PlatformSurfaceModel>> = processor.activeSurfaces

    private fun ensureSurfaceCreated(surfaceId: String = "daily_briefing") {
        if (!isSurfaceCreated) {
            val createSurfaceEnvelope = """{"version":"v0.9.1","createSurface":{"surfaceId":"$surfaceId","catalogId":"https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"}}"""
            try {
                processor.processInput(parser, createSurfaceEnvelope)
                isSurfaceCreated = true
                println("A2UI: Created surface $surfaceId")
            } catch (e: Exception) {
                println("A2UI: Pre-creating surface error: ${e.message}")
            }
        }
    }

    override fun process(json: String) {
        val raw = json.trim()
        if (raw.isEmpty()) return
        println("Processing A2UI JSON: $raw")

        try {
            var cleaned = raw
            if (cleaned.contains("```")) {
                cleaned = cleaned.replace(Regex("```(?:json)?"), "").replace("```", "").trim()
            }
            cleaned = cleaned.trim()

            // 1. Ensure the daily_briefing surface is created in the processor
            ensureSurfaceCreated("daily_briefing")

            // 2. Normalize whatever the LLM generated into valid A2UI v0.9.1 envelopes
            val envelopes = normalizeToEnvelopes(cleaned)
            for (envelope in envelopes) {
                println("Sending normalized envelope to A2UI processor: $envelope")
                processor.processInput(parser, envelope)
            }
        } catch (e: Exception) {
            println("Error processing A2UI: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Flattens nested component hierarchies (where children may be JSONObjects instead of string IDs)
     * into a single flat list of A2uiComponentPayload conforming to the v0.9.1 specification.
     */
    private fun flattenComponents(rawArray: JSONArray): JSONArray {
        val result = JSONArray()
        val allComponents = mutableListOf<JSONObject>()
        val seenIds = mutableSetOf<String>()

        fun collect(obj: JSONObject) {
            val children = obj.optJSONArray("children")
            if (children != null) {
                val childIds = JSONArray()
                val nestedChildrenToCollect = mutableListOf<JSONObject>()
                for (i in 0 until children.length()) {
                    val child = children.opt(i)
                    if (child is JSONObject) {
                        val childId = child.optString("id").ifEmpty { "comp_${allComponents.size}" }
                        child.put("id", childId)
                        childIds.put(childId)
                        nestedChildrenToCollect.add(child)
                    } else if (child is String) {
                        childIds.put(child)
                    }
                }
                obj.put("children", childIds)
                for (nested in nestedChildrenToCollect) {
                    collect(nested)
                }
            }

            if (!obj.has("component")) {
                obj.put("component", "Text")
            }

            // Normalize Text variant to basic catalog enum: h1, h2, h3, h4, h5, caption, body
            if (obj.optString("component").equals("Text", ignoreCase = true) && obj.has("variant")) {
                val v = obj.optString("variant").lowercase()
                val normalizedVariant = when {
                    v.startsWith("h1") -> "h1"
                    v.startsWith("h2") -> "h2"
                    v.startsWith("h3") -> "h3"
                    v.startsWith("h4") -> "h4"
                    v.startsWith("h5") -> "h5"
                    v.contains("caption") -> "caption"
                    else -> "body"
                }
                obj.put("variant", normalizedVariant)
            }

            val id = obj.optString("id").ifEmpty { "comp_${seenIds.size}" }
            obj.put("id", id)

            if (!seenIds.contains(id)) {
                seenIds.add(id)
                allComponents.add(obj)
            }
        }

        for (i in 0 until rawArray.length()) {
            val item = rawArray.optJSONObject(i) ?: continue
            collect(item)
        }

        for (comp in allComponents) {
            result.put(comp)
        }
        return result
    }

    /**
     * Converts raw LLM output (whether an envelope, an array of envelopes, an array of components,
     * or a single root component) into valid v0.9.1 A2UI message envelopes.
     */
    private fun normalizeToEnvelopes(raw: String): List<String> {
        val envelopes = mutableListOf<String>()

        // 1. Try parsing as JSONArray
        val arrayStart = raw.indexOf('[')
        val arrayEnd = raw.lastIndexOf(']')
        if (arrayStart != -1 && arrayEnd > arrayStart) {
            val arrayStr = raw.substring(arrayStart, arrayEnd + 1)
            try {
                val jsonArray = JSONArray(arrayStr)
                if (jsonArray.length() > 0) {
                    val firstItem = jsonArray.optJSONObject(0)
                    if (firstItem != null && firstItem.has("version")) {
                        // Array of message envelopes
                        for (i in 0 until jsonArray.length()) {
                            val envObj = jsonArray.getJSONObject(i)
                            if (envObj.has("updateComponents")) {
                                val uc = envObj.getJSONObject("updateComponents")
                                if (uc.has("components")) {
                                    uc.put("components", flattenComponents(uc.getJSONArray("components")))
                                }
                            }
                            envelopes.add(envObj.toString())
                        }
                        return envelopes
                    } else if (firstItem != null && (firstItem.has("component") || firstItem.has("id"))) {
                        // Array of components (e.g. [ { "id": "root", ... } ])
                        val flatComponents = flattenComponents(jsonArray)
                        val envelope = JSONObject().apply {
                            put("version", "v0.9.1")
                            put("updateComponents", JSONObject().apply {
                                put("surfaceId", "daily_briefing")
                                put("components", flatComponents)
                            })
                        }
                        return listOf(envelope.toString())
                    }
                }
            } catch (e: Exception) {
                println("A2UI: JSONArray normalization note: ${e.message}")
            }
        }

        // 2. Try parsing as JSONObject
        val objStart = raw.indexOf('{')
        val objEnd = raw.lastIndexOf('}')
        if (objStart != -1 && objEnd > objStart) {
            val objStr = raw.substring(objStart, objEnd + 1)
            try {
                val jsonObj = JSONObject(objStr)
                if (jsonObj.has("updateComponents") || jsonObj.has("createSurface")) {
                    if (!jsonObj.has("version")) {
                        jsonObj.put("version", "v0.9.1")
                    }
                    if (jsonObj.has("updateComponents")) {
                        val uc = jsonObj.getJSONObject("updateComponents")
                        if (!uc.has("surfaceId")) {
                            uc.put("surfaceId", "daily_briefing")
                        }
                        if (uc.has("components")) {
                            uc.put("components", flattenComponents(uc.getJSONArray("components")))
                        }
                    }
                    return listOf(jsonObj.toString())
                }

                if (jsonObj.has("components")) {
                    val flatComponents = flattenComponents(jsonObj.getJSONArray("components"))
                    val envelope = JSONObject().apply {
                        put("version", "v0.9.1")
                        put("updateComponents", JSONObject().apply {
                            put("surfaceId", "daily_briefing")
                            put("components", flatComponents)
                        })
                    }
                    return listOf(envelope.toString())
                }

                if (jsonObj.has("component") || jsonObj.has("id")) {
                    val rawArray = JSONArray().apply { put(jsonObj) }
                    val flatComponents = flattenComponents(rawArray)
                    val envelope = JSONObject().apply {
                        put("version", "v0.9.1")
                        put("updateComponents", JSONObject().apply {
                            put("surfaceId", "daily_briefing")
                            put("components", flatComponents)
                        })
                    }
                    return listOf(envelope.toString())
                }
            } catch (e: Exception) {
                println("A2UI: JSONObject normalization note: ${e.message}")
            }
        }

        // 3. Fallback to repairIncompleteEnvelope if truncated
        val repaired = repairIncompleteEnvelope(raw)
        if (repaired != null) {
            return listOf(repaired)
        }

        return envelopes
    }

    /**
     * Best-effort auto repair for JSON envelope cut off by token limit.
     */
    private fun repairIncompleteEnvelope(raw: String): String? {
        return try {
            val surfaceMatch = Regex("\"surfaceId\"\\s*:\\s*\"([^\"]+)\"").find(raw)
            val surfaceId = surfaceMatch?.groupValues?.get(1) ?: "daily_briefing"

            val componentObjects = mutableListOf<String>()
            var cur = 0
            while (cur < raw.length) {
                val objStart = raw.indexOf('{', cur)
                if (objStart == -1) break
                var depth = 0
                var objEnd = -1
                var inString = false
                var escape = false
                for (j in objStart until raw.length) {
                    val c = raw[j]
                    if (escape) { escape = false; continue }
                    if (c == '\\') { escape = true; continue }
                    if (c == '"') { inString = !inString; continue }
                    if (!inString) {
                        if (c == '{') depth++
                        else if (c == '}') {
                            depth--
                            if (depth == 0) { objEnd = j; break }
                        }
                    }
                }
                if (objEnd != -1) {
                    val sub = raw.substring(objStart, objEnd + 1)
                    if (sub.contains("\"component\"") || sub.contains("\"id\"")) {
                        componentObjects.add(sub)
                    }
                    cur = objEnd + 1
                } else {
                    break
                }
            }

            if (componentObjects.isEmpty()) return null

            val rawArray = JSONArray()
            for (compStr in componentObjects) {
                try {
                    rawArray.put(JSONObject(compStr))
                } catch (_: Exception) {}
            }

            val flatComponents = flattenComponents(rawArray)
            if (flatComponents.length() == 0) return null

            val envelope = JSONObject().apply {
                put("version", "v0.9.1")
                put("updateComponents", JSONObject().apply {
                    put("surfaceId", surfaceId)
                    put("components", flatComponents)
                })
            }
            envelope.toString()
        } catch (e: Exception) {
            println("Envelope repair failed: ${e.message}")
            null
        }
    }
}
