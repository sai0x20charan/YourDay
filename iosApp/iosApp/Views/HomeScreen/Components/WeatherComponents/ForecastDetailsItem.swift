//
//  ForecastDetailsItem.swift
//  iosApp
//
//  Created by Sai Charan on 07/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import Shared

struct WeatherForecastItem: View {
    let forecastData: [ForecastWeatherState]
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 0) {
                ForEach(forecastData, id: \.self) { item in
                    VStack(alignment: .center, spacing: 5) {
                        Text(item.time ?? "")
                            .font(.body)
                        if let temperatureIcon = item.icon {
                            Image(resource : temperatureIcon)
                                .resizable()
                                .frame(width: 24, height: 24)
                            
                        }
                        
                        
                        
                        Text(String(item.temp))
                                .font(.body)
                        
                        
                        
                            Text(item.condition)
                                .font(.caption)
                        
                    }
                    .padding(8)
                    Divider()
                        .padding(.vertical,10)
                        .padding(.horizontal,5)
                }
                
            }
        }
    }
}

