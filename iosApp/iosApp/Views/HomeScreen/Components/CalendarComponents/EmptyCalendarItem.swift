//
//  EmptyCalenderItem.swift
//  iosApp
//
//  Created by Sai Charan on 01/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct EmptyCalendarView: View {
    var body: some View {
        VStack {
            Image(resource: MR.images.shared.calender)
                .resizable()
                .frame(width: 50, height: 52)
                .padding(.bottom)
            
            Text("Your calendar is clear")
                .font(.subheadline)
                .fontWeight(.bold)
            
            Text("Enjoy your peaceful day ahead")
                .font(.caption2)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}
