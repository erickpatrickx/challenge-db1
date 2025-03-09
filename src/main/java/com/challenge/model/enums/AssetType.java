package com.challenge.model.enums;

public enum AssetType {
    IMAGE, VIDEO;

    public static AssetType fromFileExtension(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return IMAGE;
            case "mp4":
            case "avi":
            case "mov":
            case "mkv":
                return VIDEO;
            default:
                throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
        }
    }
}
