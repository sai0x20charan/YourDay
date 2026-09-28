//
//  TodoItem.swift
//  iosApp
//
//  Created by Sai Charan on 28/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct TodoDetailsItem: View {
    @State var todoData: Shared.TodoDataState
    var onTodoOpen: ((_ url : String) -> Void )

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment : .firstTextBaseline) {
                
                if let attributedString = try? AttributedString(markdown: todoData.taskName ?? "") {
                    Text(attributedString)
                        .frame(maxWidth: .infinity, alignment: .leading)
                } else {
                    Text(todoData.taskName ?? "")
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                
                if todoData.isOverDue == true {
                    RoundedChip(title: "OverDue")

                }
            }

            HStack {
                if let logo = todoData.todoImage {
                    Image(resource: logo)
                        .resizable()
                        .frame(width: 20, height: 20)
                        .clipShape(Circle())
                } else {
                    Image(systemName: "questionmark.circle")
                        .resizable()
                        .frame(width: 20, height: 20)
                        .foregroundColor(.gray)
                }

                Text(todoData.todoProvider ?? "Unknown Provider")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Spacer()

                Text(todoData.date ?? "Unknown Date")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
        .onTapGesture {
                onTodoOpen(todoData.taskLink)
            
        }
        Divider()
    }

}
