package com.app.util;

public class SimilarityRequest {
    private String resume;
    private String jobDescription;

    public SimilarityRequest(String resume, String jobDescription) {
        this.resume = resume;
        this.jobDescription = jobDescription;
    }

    public String getResume() {
        return resume;
    }

    public String getJobDescription() {
        return jobDescription;
    }
}

