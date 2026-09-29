//
//  OnBoardingScreen.swift
//  iosApp
//
//  Created by Sai Charan on 13/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Combine
import EventKit
import PermissionsKit
import CalendarPermission
import LocationPermission

struct OnBoardingScreen: View {
    let component: Shared.OnBoardingScreenComponent
    @State var onBoardingState: Shared.OnBoardingState?
    
    init(_ component: Shared.OnBoardingScreenComponent) {
        self.component = component
    }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    VStack(spacing: 16) {
                        Image(resource: MR.images.shared.app_logo_transparent)
                            .font(.system(size: 64))
                            .foregroundStyle(.yellow, .orange)
                        
                        Text("Welcome to Your Day")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text("Your companion for planning your day with current weather conditions, events, and tasks.")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding(.horizontal, 32)
                    .padding(.vertical, 40)
                    
                    Divider()
                    
                    PermissionRow(
                        title: "Weather Insights",
                        description: "Real-time weather updates to plan your day",
                        systemImage: "location.fill",
                        buttonTitle: "Enable Location",
                        action: {
                            component.onEvent(event: OnBoardingEventRequestLocationPermission.shared)
                        },
                        isPermissionGranted: onBoardingState?.isLocationPermissionGranted == true
                    )
                    
                    Divider()
                        .padding(.leading, 72)
                    
                    PermissionRow(
                        title: "Calendar Sync",
                        description: "Never miss important events and meetings",
                        systemImage: "calendar",
                        buttonTitle: "Grant Access",
                        action: {
                            component.onEvent(event: OnBoardingEventRequestCalendarPermission.shared)
                        },
                        isPermissionGranted: onBoardingState?.isCalendarPermissionGranted == true
                    )
                    
                    Divider()
                        .padding(.leading, 72)
                    
                    PermissionRow(
                        title: "Task Management",
                        description: "See all your Todoist tasks in one place",
                        systemImage: "checklist",
                        buttonTitle: "Connect Todoist",
                        action: {
                            component.onEvent(event: OnBoardingEventConnectTodoist.shared)
                        },
                        isPermissionGranted: onBoardingState?.isTodoistConnected == true
                    )
                    
                    Divider()
                }
            }
            .safeAreaInset(edge: .bottom) {
                Button {
                    component.onEvent(event: OnBoardingEventFinish.shared)
                } label: {
                    Text("Get Started")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.large)
                .padding()
            }
            .navigationBarTitleDisplayMode(.inline)
        }
        .onAppear {
            observeState()
        }
    }
    
    private func observeState() {
        Task {
            for await state in component.onBoardingState {
                await MainActor.run {
                    self.onBoardingState = state
                }
            }
        }
    }
}

struct PermissionRow: View {
    let title: String
    let description: String
    let systemImage: String
    let buttonTitle: String
    let action: () -> Void
    let isPermissionGranted: Bool

    var body: some View {
        HStack(spacing: 0) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(isPermissionGranted ? Color.green.opacity(0.15) : Color.blue.opacity(0.15))
                    .frame(width: 48, height: 48)
                
                Image(systemName: systemImage)
                    .font(.title3)
                    .foregroundStyle(isPermissionGranted ? .green : .blue)
            }
            .padding(.horizontal, 16)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                
                Text(description)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .padding(.vertical, 16)
            
            Spacer()
            Group {
                if isPermissionGranted {
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundStyle(.green)
                        .font(.title3)
                } else {
                    Button(action: action) {
                        Text(buttonTitle)
                    }
                    .buttonStyle(.borderedProminent)
                }
            }
            .padding(.horizontal, 16)
        }
    }
}
