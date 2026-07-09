// ===== addRecipe 示例 =====

// 1 个输入
mods.advancedbotany.AdvancedPlate.addRecipe(<minecraft:diamond>, [<minecraft:coal>], 10000, 0x00FFFF);

// 2 个输入
mods.advancedbotany.AdvancedPlate.addRecipe(<minecraft:emerald>, [<minecraft:coal>, <minecraft:iron_ingot>], 15000, 0x00FF00);

// 3 个输入
mods.advancedbotany.AdvancedPlate.addRecipe(<minecraft:nether_star>, [<minecraft:coal>, <minecraft:iron_ingot>, <minecraft:gold_ingot>], 20000, 0xFF00FF);

// ===== 以下方法请按需单独测试，不要和 addRecipe 同时执行 =====

// removeRecipe — 删除指定输出的配方
mods.advancedbotany.AdvancedPlate.removeRecipe(<botania:manaresource:4>);

// removeAll — 删除所有配方
// mods.advancedbotany.AdvancedPlate.removeAll();

// modifyMana — 修改指定配方的魔力消耗
mods.advancedbotany.AdvancedPlate.modifyMana(<advanced_botany:itemabresource>, 50000000);

// modifyOutput — 修改配方的输出物品
// mods.advancedbotany.AdvancedPlate.modifyOutput(<minecraft:diamond>, <minecraft:emerald>);

// modifyColor — 修改配方的颜色
// mods.advancedbotany.AdvancedPlate.modifyColor(<minecraft:diamond>, 0xFF0000);

// ===== 古阿尔菲林配方示例 =====

// 1. addRecipe — 添加配方（输出, 输入, 概率 0.0~1.0）
mods.advancedbotany.Alphirine.addRecipe(<minecraft:diamond>, <minecraft:coal>, 0.15);

// 2. removeRecipe — 删除指定输出的配方
// mods.advancedbotany.Alphirine.removeRecipe(<minecraft:diamond>);

// 3. removeAll — 删除所有配方
// mods.advancedbotany.Alphirine.removeAll();

// 4. modifyChance — 修改指定配方的概率
// mods.advancedbotany.Alphirine.modifyChance(<minecraft:diamond>, 0.5);

// 5. modifyOutput — 修改配方的输出物品
// mods.advancedbotany.Alphirine.modifyOutput(<minecraft:diamond>, <minecraft:emerald>);

// 6. modifyInput — 修改配方的输入物品
// mods.advancedbotany.Alphirine.modifyInput(<minecraft:diamond>, <minecraft:iron_ingot>);