//
//  PermissionObserver.swift
//  iosApp
//
//  Created by Sai Charan on 01/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import CoreLocation
import EventKit
import Shared
import UIKit
import PermissionsKit
import CalendarPermission

class PermissionObserver: NSObject, ObservableObject, CLLocationManagerDelegate{
    private var locationManager = CLLocationManager()
    private var eventKit = EKEventStore()
    
    @Published var locationPermission: Shared.PermissionState = Shared.PermissionState.notDetermined
    @Published var calendarPermission: Shared.PermissionState = Shared.PermissionState.notDetermined
    
    override init() {
        super.init()
        locationManager.delegate = self
        checkCalendarPermission()
        checkLocationPermission()
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(appDidBecomeActive),
            name: UIApplication.didBecomeActiveNotification,
            object: nil
        )
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    func checkLocationPermission() {
        switch locationManager.authorizationStatus {
        case .authorizedWhenInUse, .authorizedAlways:
            
            locationPermission = .granted
        case .denied, .restricted:
            locationPermission = .notGranted
        case .notDetermined:
            locationPermission = .notDetermined
        @unknown default:
            print("Unknown location permission status")
        }
    }
    
    @objc func appDidBecomeActive() {
        checkCalendarPermission()
    }
    
    func checkCalendarPermission() {
        
        let status = Permission.calendar(access: .full).status
        switch status {
        case .authorized:
            print("Calender Permission Is given")
            calendarPermission = .granted
        case .denied:
            print("Calender Permission Is not given")
            calendarPermission = .notGranted
        case .notDetermined:
            print("Calener Permission Is not determined")
            calendarPermission = .notDetermined
        default:
            print("Unknown calendar permission status")
        }
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        
        checkLocationPermission()
    }

    
    

}
