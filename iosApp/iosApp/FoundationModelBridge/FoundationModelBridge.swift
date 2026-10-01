//
//  FoundationModel.swift
//  iosApp
//
//  Created by Sai Charan on 9/30/26.
//  Copyright © 2026 orgName. All rights reserved.
//

import Foundation
import FoundationModels

@available(iOS 26.0, *)
@objc(FoundationModelBridge)
public class FoundationModelBridge : NSObject {

    private var session: LanguageModelSession = LanguageModelSession()

    @objc public func generateResponse(
        _ prompt : String,
        completion : @escaping (String? , Error?) -> Void
    ) {
        Task {
            do {
                var lastContent: String = ""
                for try await response in session.streamResponse(to: prompt) {
                    lastContent = response.content
                    
                    completion(response.content, nil)
                }
                _ = lastContent
            } catch {
                completion(nil, error)
            }
        }
    }
}
