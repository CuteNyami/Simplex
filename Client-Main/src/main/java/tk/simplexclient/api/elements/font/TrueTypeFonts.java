package tk.simplexclient.api.elements.font;

import lombok.Getter;

public enum TrueTypeFonts {
    PRODUCT_SANS("/assets/minecraft/simplex/fonts/product_sans.ttf"),
    INTER("/assets/minecraft/simplex/fonts/inter-regular.ttf"),
    INTER_BOLD("/assets/minecraft/simplex/fonts/inter-bold.ttf"),
    INTER_THIN("/assets/minecraft/simplex/fonts/inter-thin.ttf");

    @Getter
    private final String path;

    TrueTypeFonts(String path) {
        this.path = path;
    }
}