//
//  SettingsScreen.swift
//  iosApp
//
//  Created by Sai Charan on 17/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SettingsScreen: View {
    var component: SettingsScreenComponent
    @State private var state: Shared.SettingsState?

    var body: some View {
        List {
            // MARK: - Weather
            Section("Weather") {
                Picker("Temperature Units", selection: Binding(
                    get: { state?.weatherUnits ?? "" },
                    set: { component.onEvent(event: Shared.SettingsEvents.OnChangeWeatherUnits(weatherUnit: $0)) }
                )) {
                    ForEach(Shared.WeatherUnitsEnums.allCases, id: \.self) { item in
                        let label = item == .c ? WeatherUnits.shared.C : WeatherUnits.shared.F
                        Text(label).tag(label)
                    }
                }
            }

            // MARK: - Tasks
            Section("Tasks") {
                LabeledContent("Todoist Integration") {
                    Button(state?.isTodoistConnected == true ? "Disconnect" : "Connect") {
                        component.onEvent(event: Shared.SettingsEvents.TodoConnect.shared)
                    }
                    .tint(state?.isTodoistConnected == true ? .red : .accentColor)
                }
            }

            // MARK: - AI
            Section("AI") {
                LabeledContent("Qwen3.5 2B Q4_K_M") {
                    if state?.isAiModelDownloading == true {
                        ProgressView()
                    } else if state?.isAIModelDownloaded == true {
                        Button(role: .destructive) {
                            component.onEvent(event: SettingsEvents.OnDeleteAIModel())
                        } label: {
                            Label("Delete", systemImage: "trash")
                                .labelStyle(.iconOnly)
                        }
                    } else {
                        Button {
                            component.onEvent(event: SettingsEvents.OnDownloadAIModel())
                        } label: {
                            Label("Download", systemImage: "arrow.down.circle")
                                .labelStyle(.iconOnly)
                        }
                    }
                }
            }

            // MARK: - About
            Section("About") {
                LabeledContent("App Version", value: state?.appVersion ?? "—")
            }
        }
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.large)
        .toolbarRole(.editor)
        .task { await observeState() }
    }

    private func observeState() async {
        for await settingsState in component.settingsState {
            state = settingsState
        }
    }
}
