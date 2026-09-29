import SwiftUI
import Firebase
import Shared

@main
struct iOSApp: App {
    
    @State private var componentHolder: ComponentHolder<RootComponent>
    @State private var authentizationId : String? = nil
    @State private var errorCode : String? = nil

    init() {
        FirebaseApp.configure()
        KointInitHelper().doInitKoin()
        
        _componentHolder = State(initialValue: ComponentHolder { context in
            RootComponent(
                authorizationId: nil,
                errorCode: nil,
                componentContext: context
            )
        })
    }

    var body: some Scene {
        WindowGroup {
            RootView(componentHolder.component)
                .onOpenURL { url in
                    var code: String? = nil
                    var errorCode: String? = nil

                    if let codeParam = url.absoluteString.components(separatedBy: "code=").last {
                        code = codeParam.components(separatedBy: ",").first
                    }

                    if let errorParam = url.absoluteString.components(separatedBy: "error=").last {
                        errorCode = errorParam.components(separatedBy: ",").first
                    }

                    self.componentHolder = ComponentHolder { context in
                        RootComponent(
                            authorizationId: code,
                            errorCode: errorCode,
                            componentContext: context
                        )
                    }
                }
        }
    }
}
