//
//  HomeScreenView.swift
//  iosApp
//
//  Created by Sai Charan on 27/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import Shared
import Combine
import EventKit

struct HomeScreenView: View {
    private let component: HomeScreenComponent
    @State private var homeState: Shared.HomeState?

    @ObservedObject private var permissionObserver: PermissionObserver = .init()
    @State private var permissionState: PermissionState?

    init(_ component: HomeScreenComponent) {
        self.component = component
        permissionState = permissionObserver.locationPermission
    }

    var body: some View {
        NavigationView {
            ScrollView {
                LazyVStack() {
                    VStack(alignment: .leading) {
                        Text(DateUtils().getGreeting())
                            .font(.title2)
                            .bold()
                        Text(DateUtils().getDateInDDMMYYYY())
                            .bold()
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    
                    WeatherCard(
                        weatherState: Binding(
                            get: { homeState?.weatherState },
                            set: { _ in }
                        )
                    ) {
                        component.onEvent(
                            event: HomeEventRequestLocationPermission.shared
                        )
                    }
                    
                    CalendarCard(
                        calendarState: Binding(
                            get: { homeState?.calendarData },
                            set: { _ in }
                        )
                    ) {
                        component.onEvent(
                            event: HomeEventRequestCalendarPermission.shared
                        )
                    }
                    .padding(.vertical, 8)
                    
                    TodoCard(
                        onConnectClick: {
                            component.onEvent(event: HomeEventConnectTodoist.shared)
                        },
                        todoState: Binding(
                            get: { homeState?.todoState },
                            set: { _ in }
                        ),
                        onTodoOpen: { link in
                            component.onEvent(event: Shared.HomeEventOnOpenLink(url: link))
                        }
                    )
                }
            }
        }
        .toolbar {
            ToolbarItem {
                Menu("more", systemImage: "ellipsis.circle") {
                    Button("Settings") {
                        component.onEvent(event: Shared.HomeEventOpenSettingsPage.shared)
                    }
                }
            }
        }
        .refreshable {
            component.onEvent(event: HomeEventRefreshData.shared)
        }
        .onAppear {
            observeState()
            observePermissionRequest()
        }
        .onReceive(permissionObserver.$locationPermission) { permissionState in
            switch permissionState {
            case .granted:
                component.onEvent(event: HomeEventFetchWeather.shared)
            default:
                break
            }
        }
        .onReceive(permissionObserver.$calendarPermission) { permissionState in
            switch permissionState {
            case .granted:
                component.onEvent(event: HomeEventFetchCalendarEvents.shared)
            default:
                break
            }
        }
    }
    
    private func observePermissionRequest() {
        Task {
            for await effect in component.effects {
                switch effect {
                case let toastEffect as Shared.HomeEffectShowToast:
                    break
                default:
                    break
                }
            }
        }
    }

    private func observeState() {
        Task {
            for await state in component.state {
                await MainActor.run {
                    self.homeState = state
                }
            }
        }
    }
}
