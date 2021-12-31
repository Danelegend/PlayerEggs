package me.danelegend.playereggs.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

public class PlayerSkinManager {

    private HashMap<String, String[]> playerSkins;
    private HashMap<String, String> actualPlayerNames;


    public PlayerSkinManager() {
        this.playerSkins = new HashMap<>();
        this.actualPlayerNames = new HashMap<>();
    }

    public String[] getPlayerTexture(String name) {
        if (playerSkins.get(name) == null) {
            addTexture(name);
        }

        return playerSkins.get(name);
    }

    public String getActualPlayerName(String name) {
        return actualPlayerNames.get(name);
    }

    // Accessing the webpage is likely what causes the most delay
    // Play around and see if there is a way to speed this up.
    public void addTexture(String name) {
        // We need to get the player's texture
        try {
            // Gets the profile page to the user we want
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);

            // Reads the data on the profile page and gets the uuid and actual name
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            JsonObject job = new JsonParser().parse(reader_0).getAsJsonObject();
            String uuid = job.get("id").getAsString();
            String actualName = job.get("name").getAsString();

            // Gets the profile page given the uuid and gets the value and signature of the skin
            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            String[] skin = {texture, signature};

            // Adds the data to their respective HashMaps - O(1) lookup.
            playerSkins.put(name, skin);
            actualPlayerNames.put(name, actualName);
        } catch (IOException e) {
            Logger logger = Logger.getLogger("Minecraft");

            logger.info("[PlayerEggs]: Could not retrieve player skin. Are you connected to the internet");
            e.printStackTrace();
        }
    }

}
