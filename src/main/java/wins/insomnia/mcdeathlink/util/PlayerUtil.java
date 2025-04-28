package wins.insomnia.mcdeathlink.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class PlayerUtil {

	public static final HashMap<UUID, UserCacheData> USER_CACHE = new HashMap<>();


	public static String getUsernameFromUUID(UUID uuid) {

		UserCacheData cacheData = USER_CACHE.get(uuid);
		if (cacheData != null) {
			return cacheData.name();
		}

		try {
			URL url = new URL("https://api.minecraftservices.com/minecraft/profile/lookup/" + uuid.toString().replace("-", ""));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			// Parse the JSON response
			Gson gson = new Gson();
			JsonObject playerInfo = gson.fromJson(response.toString(), JsonObject.class);

			UserCacheData newCacheData = new UserCacheData(uuid, playerInfo.get("name").getAsString());

			USER_CACHE.put(uuid, newCacheData);

			return newCacheData.name();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public record UserCacheData(UUID id, String name) {};
}

