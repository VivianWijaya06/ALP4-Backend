# OpenAI Integration Guide

This document explains how the CookEasy backend integrates with OpenAI's API to enable the ChefBot feature.

## Overview

The `ChefBotService` class handles interactions with the OpenAI API. It provides natural language-based cooking help by sending user messages to OpenAI and returning the AI's responses.

## Files Involved

- `config/OpenAIConfig.java` – Configures the OpenAI API client.
- `service/ChefBotService.java` – Contains the logic to call OpenAI's API and handle responses.
- `controller/ChatBotController.java` – REST controller that exposes the chatbot endpoint.

## Setup Instructions

1. **Create an account on [OpenAI](https://platform.openai.com/signup)** if you haven’t already.
2. **Generate an API key** from the [OpenAI API dashboard](https://platform.openai.com/account/api-keys).
3. **Set the API key** in your `application.properties` file like so:

   ```properties
   openai.api.key=your_openai_api_key_here
   ```

4. **Adjust other OpenAI settings** (optional):

   ```properties
   openai.model=gpt-3.5-turbo
   openai.api.url=https://api.openai.com/v1/chat/completions
   ```

## How It Works

When a user sends a message to the `/chatbot` endpoint, the message is forwarded to `ChefBotService`, which creates a chat completion request to OpenAI using the GPT model. The AI response is then returned as a reply to the user.

## Example Request

```bash
POST /chatbot
Content-Type: application/json

{
  "message": "How do I cook beef wellington?"
}
```

## Example Response

```json
{
  "reply": "To cook a beef wellington, you'll need..."
}
```

## Notes

- Rate limits and costs apply based on your OpenAI account.
- Ensure sensitive keys are not hard-coded or exposed in version control.

## Troubleshooting

- **401 Unauthorized**: Check your API key in `application.properties`.
- **Timeouts or slow responses**: Try a simpler prompt or check your network/API rate limits.