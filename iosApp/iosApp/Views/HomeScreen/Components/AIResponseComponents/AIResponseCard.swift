//
//  AIResponseCard.swift
//  iosApp
//
//  Created by Sai Charan on 2/21/26.
//  Copyright © 2026 orgName. All rights reserved.
//
//
//  AIResponseCard.swift
//  iosApp
//

import SwiftUI
import Shared

struct AIResponseCard: View {
    let thinkingResponse : String
    let aiResponse : String
    let isThinking : Bool
    let showThinking : Bool
    let onExpandToggle : (() -> Void)
    var body: some View {
        GroupBox{
            VStack(alignment: .leading, spacing: 10) {
                if !thinkingResponse.isEmpty {
                    AIThinkingView(
                        thinkingText: thinkingResponse,
                        isExpanded: showThinking,
                        isThinking: isThinking,
                        onExpandToggle: onExpandToggle
                    )
                }

                if aiResponse.isEmpty {
                    ShimmerLines()
                } else {
                    if let attributedString = try? AttributedString(markdown:  self.aiResponse) {
                        Text(attributedString)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .font(.body)
                            .foregroundStyle(.primary)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .transition(.opacity)
                            .animation(.easeIn(duration: 0.3), value: aiResponse)

                    } else {
                        Text(self.aiResponse)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .font(.body)
                            .foregroundStyle(.primary)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .transition(.opacity)
                            .animation(.easeIn(duration: 0.3), value: aiResponse)

                    }
                                            
                        
                }
            }
        }
        .padding(.horizontal)
    }
}


struct AIThinkingView: View {
    let thinkingText: String
    let isExpanded: Bool
    let isThinking : Bool
    var onExpandToggle: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {


            HStack(spacing: 8) {
                RoundedRectangle(cornerRadius: 2, style: .continuous)
                    .fill(.purple.opacity(0.7))
                    .frame(width: 3, height: 16)

                Text(isThinking ? "Thinking" : "Thought process")
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.purple.opacity(0.85))

                Spacer()

                Button {
                    withAnimation(.easeInOut(duration: 0.25)) {
                        onExpandToggle()
                    }
                } label: {
                    HStack(spacing: 4) {
                        Text(isExpanded ? "Hide" : "Show")
                            .font(.caption2.weight(.medium))

                        Image(systemName: "chevron.down")
                            .font(.system(size: 10, weight: .medium))
                            .rotationEffect(.degrees(isExpanded ? 180 : 0))
                            .animation(.easeInOut(duration: 0.2), value: isExpanded)
                    }
                    .foregroundStyle(.secondary)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)


            VStack(alignment: .leading, spacing: 0) {
                Divider()
                    .padding(.horizontal, 12)
                    .opacity(isExpanded ? 0.5 : 0)

                Text(thinkingText)
                    .font(.caption)
                    .italic()
                    .foregroundStyle(.secondary)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 10)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .opacity(isExpanded ? 1 : 0)
            }
            .frame(maxHeight: isExpanded ? .infinity : 0, alignment: .top)
            .clipped()
            .animation(.easeInOut(duration: 0.25), value: isExpanded)
        }
        .background(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .fill(.quaternary.opacity(0.6))
        )
    }
}


struct ShimmerLines: View {
    @State private var opacity: Double = 0.15

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ShimmerLine(widthFraction: 1.0,  opacity: opacity)
            ShimmerLine(widthFraction: 0.85, opacity: opacity)
            ShimmerLine(widthFraction: 0.6,  opacity: opacity)
        }
        .onAppear {
            withAnimation(
                .easeInOut(duration: 0.9)
                .repeatForever(autoreverses: true)
            ) {
                opacity = 0.4
            }
        }
    }
}

private struct ShimmerLine: View {
    let widthFraction: CGFloat
    let opacity: Double

    var body: some View {
        GeometryReader { geo in
            RoundedRectangle(cornerRadius: 6, style: .continuous)
                .fill(.primary.opacity(opacity))
                .frame(width: geo.size.width * widthFraction, height: 13)
        }
        .frame(height: 13)
    }
}
