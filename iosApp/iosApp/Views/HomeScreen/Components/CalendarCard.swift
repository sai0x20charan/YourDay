//
//  CalendarCard.swift
//  iosApp
//
//  Created by Sai Charan on 30/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct CalendarCard: View {
    @Binding var calendarState: Shared.CalendarState?
    var onGrant: () -> Void

    var body: some View {
        ContentCard(
            title: "Today's Events",
            isLoading: Binding(get: { calendarState?.isLoading == true }, set: { _ in }),
            hasError: Binding(get: { calendarState?.error != nil }, set: { _ in }),
            content: {
                VStack(alignment: .leading) {
                    if let calendarState = calendarState {
                        if !calendarState.isCalendarPermissionGranted {
                            GrantPermissionItem(
                                onGrant: onGrant,
                                title: "Please allow calendar permission to fetch events"
                            )
                        } else if let events = calendarState.calendarData, !events.isEmpty {
                            ForEach(events, id: \.eventId) { event in
                                CalendarEventItem(calendarEvent: event)
                            }
                        } else if calendarState.calendarData != nil {
                            EmptyCalendarView()
                        }
                    }
                }
            }
        )
    }
}

typealias CalenderCard = CalendarCard
