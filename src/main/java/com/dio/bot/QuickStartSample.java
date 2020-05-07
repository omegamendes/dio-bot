package com.dio.bot;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class QuickStartSample {

    /** Demonstrates using the Text-to-Speech API. */
    public static String getAudio(String wordToSay) {
        // Instantiates a client
        try {
            try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
                // Set the text input to be synthesized
                SynthesisInput input = SynthesisInput.newBuilder().setText(wordToSay).build();

                // Build the voice request, select the language code ("en-US") and the ssml voice gender
                // ("neutral")
                VoiceSelectionParams voice =
                        VoiceSelectionParams.newBuilder()
                                .setLanguageCode("pt-br")
                                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                                .build();

                // Select the type of audio file you want returned
                AudioConfig audioConfig =
                        AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

                // Perform the text-to-speech request on the text input with the selected voice parameters and
                // audio file type
                SynthesizeSpeechResponse response =
                        textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

                // Get the audio contents from the response
                ByteString audioContents = response.getAudioContent();

                // Write the response to the output file.
                try (OutputStream out = new FileOutputStream("output.mp3")) {
                    out.write(audioContents.toByteArray());
                    System.out.println("Audio content written to file \"output.mp3\"");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "output.mp3";
    }
}
