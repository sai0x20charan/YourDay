//
//  FoundationModelBridge.h
//  iosApp
//
//  Created by Sai Charan on 9/30/26.
//  Copyright © 2026 orgName. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^FoundationModelCompletion)(NSString * _Nullable response,
                                           NSError * _Nullable error);

API_AVAILABLE(ios(26.0))
@interface FoundationModelBridge : NSObject

- (void)generateResponse:(NSString *)prompt
              completion:(FoundationModelCompletion)completion;

@end

NS_ASSUME_NONNULL_END
