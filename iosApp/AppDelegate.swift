//
//  AppDelegate.swift
//  iosApp
//
//  Created by Sai Charan on 2/22/26.
//  Copyright © 2026 orgName. All rights reserved.
//

import Foundation
import Shared
import UIKit

class AppDelegate: NSObject, UIApplicationDelegate {
    let root: RootComponent = RootComponent(
        componentContext: DefaultComponentContext(lifecycle: ApplicationLifecycle())
    )
}
