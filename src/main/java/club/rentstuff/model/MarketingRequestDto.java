package club.rentstuff.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MarketingRequestDto {

 @NotBlank
 @JsonProperty("company_name")
 String companyName;

 @NotBlank
 @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
 String domain;

 @NotBlank
 @Pattern(regexp = "^https?://.*")
 String website;

 @NotBlank
 String category;

 @NotBlank
 @JsonProperty("focus_keyword")
 String focusKeyword;

 @NotNull
 @JsonProperty("twitter")
 TwitterCredentials twitter;

 // Nested static class for Twitter credentials
 @Value
 @Builder
 public static class TwitterCredentials {
     @NotBlank
     @JsonProperty("api_key")
     String apiKey;

     @NotBlank
     @JsonProperty("api_secret")
     String apiSecret;

     @NotBlank
     @JsonProperty("access_token")
     String accessToken;

     @NotBlank
     @JsonProperty("access_token_secret")
     String accessTokenSecret;

     @NotBlank
     @JsonProperty("bearer_token")
     String bearerToken;
 }
}