import json, os
base = "src/main/resources/assets/minecraft"

def w(path, data):
    p = os.path.join(base, path)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w") as f:
        json.dump(data, f, indent=2)

# item look (what you see in inventory / hand)
items = {
    "blue_glazed_terracotta": "minecraft:item/elytra",
    "slime_ball": "minecraft:block/ancient_debris",
    "heavy_weighted_pressure_plate": "minecraft:block/spawner",
    "sea_lantern": "minecraft:block/netherite_block",
    "wither_rose": "minecraft:item/mace",
    "sticky_piston": "minecraft:block/gilded_blackstone",
}
for k, m in items.items():
    w("items/%s.json" % k, {"model": {"type": "minecraft:model", "model": m}})

# placed block look
blocks = {
    "heavy_weighted_pressure_plate": "minecraft:block/spawner",
    "sea_lantern": "minecraft:block/netherite_block",
    "sticky_piston": "minecraft:block/gilded_blackstone",
}
for k, m in blocks.items():
    w("blockstates/%s.json" % k, {"variants": {"": {"model": m}}})

# names (pink = same color as the real Elytra / Mace / spawner names)
names = {
    "block.minecraft.blue_glazed_terracotta": "\u00a7dElytra",
    "item.minecraft.slime_ball": "Ancient Debris",
    "block.minecraft.heavy_weighted_pressure_plate": "\u00a7dSkeleton Spawner",
    "block.minecraft.sea_lantern": "Netherite Block",
    "block.minecraft.wither_rose": "\u00a7dMace",
    "block.minecraft.sticky_piston": "Gilded Blackstone",
}
# same names for every common language, so your game language can't override them
for loc in ["en_us", "en_gb", "en_au", "en_ca", "en_nz", "de_de", "de_at", "de_ch",
            "sk_sk", "cs_cz", "pl_pl", "hu_hu", "fr_fr", "fr_ca", "es_es", "es_mx",
            "it_it", "pt_br", "pt_pt", "nl_nl", "ru_ru", "tr_tr", "sl_si", "hr_hr"]:
    w("lang/%s.json" % loc, names)
print("assets written")
