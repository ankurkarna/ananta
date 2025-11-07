package org.karna.ankur.ananta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @Size(max = 2000, message = "Caption cannot exceed 2000 characters")
    private String caption;
}
