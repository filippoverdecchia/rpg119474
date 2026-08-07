package it.unicam.cs.mpgc.rpg119474.persistence;

import it.unicam.cs.mpgc.rpg119474.core.item.Armor;
import it.unicam.cs.mpgc.rpg119474.core.item.Consumable;
import it.unicam.cs.mpgc.rpg119474.core.item.Item;
import it.unicam.cs.mpgc.rpg119474.core.item.Weapon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Insegna a Gson a leggere e scrivere la gerarchia <em>sealed</em> {@link Item}.
 * <p>
 * Un oggetto e' sempre un'arma, un'armatura o un consumabile, ma nel JSON questa
 * informazione andrebbe perduta: senza saperlo, in lettura non si potrebbe
 * ricostruire il tipo corretto. L'adattatore aggiunge quindi un campo
 * {@code type} che fa da discriminante, e lo usa al contrario in deserializzazione.
 */
final class ItemJsonAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    private static final String TYPE = "type";
    private static final String WEAPON = "weapon";
    private static final String ARMOR = "armor";
    private static final String CONSUMABLE = "consumable";

    @Override
    public JsonElement serialize(Item item, Type type, JsonSerializationContext context) {
        JsonObject json = switch (item) {
            case Weapon weapon -> context.serialize(weapon, Weapon.class).getAsJsonObject();
            case Armor armor -> context.serialize(armor, Armor.class).getAsJsonObject();
            case Consumable consumable -> context.serialize(consumable, Consumable.class).getAsJsonObject();
        };
        json.addProperty(TYPE, discriminatorOf(item));
        return json;
    }

    @Override
    public Item deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        JsonObject json = element.getAsJsonObject();
        if (!json.has(TYPE)) {
            throw new JsonParseException("Oggetto senza campo '" + TYPE + "'");
        }
        String discriminator = json.get(TYPE).getAsString();
        return switch (discriminator) {
            case WEAPON -> context.deserialize(json, Weapon.class);
            case ARMOR -> context.deserialize(json, Armor.class);
            case CONSUMABLE -> context.deserialize(json, Consumable.class);
            default -> throw new JsonParseException("Tipo di oggetto non riconosciuto: " + discriminator);
        };
    }

    /** Lo switch e' esaustivo sulla gerarchia sealed: un nuovo tipo di oggetto va dichiarato qui. */
    private static String discriminatorOf(Item item) {
        return switch (item) {
            case Weapon weapon -> WEAPON;
            case Armor armor -> ARMOR;
            case Consumable consumable -> CONSUMABLE;
        };
    }
}