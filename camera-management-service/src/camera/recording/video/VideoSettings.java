package camera.recording.video;

import camera.models.FrameSettings;
import camera.models.StreamingSettings;
import camera.recording.video.file.FileSettings;
import camera.validation.ValidationSupport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VideoSettings(
        @NotNull @Valid FileSettings fileSettings,
        @NotNull @Valid FrameSettings frameSettings,
        @NotNull @Valid StreamingSettings streamingSettings
) {
    public VideoSettings(
            FileSettings fileSettings,
            FrameSettings frameSettings,
            StreamingSettings streamingSettings
    ) {
        this.fileSettings = fileSettings;
        this.frameSettings = frameSettings;
        this.streamingSettings = streamingSettings;

        ValidationSupport.validate(this);
    }
}
