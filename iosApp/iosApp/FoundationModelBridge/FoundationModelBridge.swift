//
//  FoundationModel.swift
//  iosApp
//
//  Created by Sai Charan on 9/30/26.
//  Copyright © 2026 orgName. All rights reserved.
//
//  NOTE: This bridge is for SwiftUI-side use only. Do NOT reference it from
//  Kotlin via cinterop — that creates a circular App -> Shared -> App
//  dependency and crashes on start with:
//    dyld: symbol not found in flat namespace '_OBJC_CLASS_$_FoundationModelBridge'
//  The shared `IosLocalLlmDataSource` is intentionally a stub (see Kotlin).
//  Drive Apple Intelligence from SwiftUI by instantiating this class directly.
//

import Foundation
import FoundationModels



@available(iOS 26.0, *)
@objc public class FoundationModelBridge : NSObject {

    private var session: LanguageModelSession = LanguageModelSession()


    @objc public func generateResponse(
        _ prompt : String,
        completion : @escaping (String? , Error?) -> Void
    ) {

        Task {
            do {
                var lastContent: String = ""
                for try await response in session.streamResponse(to: prompt) {
                    lastContent = response.content
                    completion(response.content, nil)
                }
                _ = lastContent
            } catch {
                completion(nil, error)
            }
        }
    }
}
