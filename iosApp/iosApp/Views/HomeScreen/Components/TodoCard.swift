//
//  TodoistCard.swift
//  iosApp
//
//  Created by Sai Charan on 11/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct TodoCard: View {
    var onConnectClick: (() -> Void)
    @Binding var todoState: Shared.TodoState?
    var onTodoOpen: ((_ url : String) -> Void )

    var body: some View {
        ContentCard(
            title: "Today's Tasks",
            isLoading: Binding(get: {todoState?.isLoading == true}, set: {_ in}),
            hasError: Binding(get: {todoState?.error != nil}, set: {_ in}),
            content: {
                VStack{
                    if let state = todoState {
                        if !state.isTodoAuthenticated {
                            TodoConnectItem(onConnectClick: onConnectClick)
                        }
                        else if let todoItems = state.todoData, !todoItems.isEmpty {
                            
                            ForEach(todoItems, id: \.id) { todoItem in
                                TodoDetailsItem(todoData: todoItem){ link in
                                    onTodoOpen(link)
                                    
                                }
                            }
                        } else if todoState?.todoData != nil {
                            EmptyTodoView()
                        }
                    } else {
                        Text("No Todoist data available")
                            .foregroundColor(.gray)
                            .font(.footnote)
                    }
                }

                
            }
        )
    }
}


