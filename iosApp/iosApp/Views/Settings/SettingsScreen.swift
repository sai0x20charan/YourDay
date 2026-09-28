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
    var component : SettingsScreenComponent
    @State var state : Shared.SettingsState?
    @State var weatherUnit : String?
    var body: some View {
        List {
                Section(
                    header: Text("Weather")
                        
                ){
                    Picker("Temperature Units", selection: Binding(
                        get: { state?.weatherUnits ?? "" },
                        set: { newValue in
                            component.onEvent(event: Shared.SettingsEvents.OnChangeWeatherUnits(weatherUnit: newValue))
                        }
                    )) {
                        ForEach(Shared.WeatherUnitsEnums.allCases, id: \.self) { item in
                            
                            let unitsString = switch item {
                            case .c: WeatherUnits.shared.C
                            case .f: WeatherUnits.shared.F
                            }

                            Text(unitsString).tag(unitsString)
                        }
                    }
                    
                    .pickerStyle(MenuPickerStyle())
                    .listRowInsets(.init(top: 0, leading: 20, bottom: 0, trailing: 20))
                    .onChange(of: state?.weatherUnits ?? "") { newValue in
                        component.onEvent(event: Shared.SettingsEvents.OnChangeWeatherUnits(weatherUnit:newValue))
                    }
                    
                    
                }
            Section(
                header: Text("Tasks")
            ) {
                HStack {
                    Text("Todoist Integration")
                    Spacer()
                    Button(state?.isTodoistConnected == false ? "Connect" : "Disconnect") {
                        component.onEvent(event: Shared.SettingsEvents.TodoConnect.shared)
                    }
                }
                
            }
            Section(
                header: Text("About App")){
                    HStack{
                        Text("App version")
                        Spacer()
                        Text(state?.appVersion ?? "")
                    }
                    
                }
                    
                    
                
                
                

            }
        .onAppear{
            observeState()
        }
            .toolbarRole(.editor)
            .navigationTitle("Settings")

        
    }
    private func observeState() {
        Task {
            for await settingsState in component.settingsState {
                state = settingsState
                weatherUnit = state?.weatherUnits
            }
        }
    }
}

