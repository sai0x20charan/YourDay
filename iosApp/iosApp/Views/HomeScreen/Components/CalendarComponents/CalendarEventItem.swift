//
//  CalendarEventItem.swift
//  iosApp
//
//  Created by Sai Charan on 30/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct CalendarEventItem: View {
    @State var calendarEvent: Shared.CalendarItems

    var body: some View {
        HStack {
            Divider()
                .frame(width: 2)
                .overlay(UIColor.fromString(calendarEvent.calendarColor ?? ""))
                .cornerRadius(8)
                .padding(.vertical, 6)
            
            VStack(alignment: .leading) {
                Text(calendarEvent.getEventName())
                    .font(.headline)
                HStack {
                    Text("\(calendarEvent.getFormatedStartTime()) - \(calendarEvent.getFormatedEndTime())")
                        .font(.caption)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, 1)
    }
}

typealias CalenderEventItem = CalendarEventItem
