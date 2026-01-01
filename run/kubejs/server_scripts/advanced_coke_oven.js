ServerEvents.recipes(event => {
    console.log("recipes:");
    event.forEachRecipe({ type: 'immersiveengineering:coke_oven'}, r => {
        var json = r.json;
        //{creosote=250, result={"item":"minecraft:charcoal"}, input={"tag":"minecraft:logs"}, time=900, type="immersiveengineering:coke_oven"}

        console.log({
            type: 'totally_lib:advanced_coke_oven',
            length: json.get("time").getAsInt() / 2,
            input: json.getAsJsonObject("input"),
            output: json.getAsJsonObject("result"),
            byproduct: [
                { fluid: "immersiveengineering:creosote", amount: json.get("creosote").getAsInt() }
            ]
        });

        event.custom({
            type: 'totally_lib:advanced_coke_oven',
            length: json.get("time").getAsInt() / 2,
            input: json.getAsJsonObject("input"),
            output: json.getAsJsonObject("result"),
            byproduct: { fluid: "immersiveengineering:creosote", amount: json.get("creosote").getAsInt() },
            power:256
        });
    });
});

//{"type":"tlib:advanced_coke_oven","length":450,"input":{"tag":"minecraft:logs"},"output":{"item":"minecraft:charcoal"},"byproduct":{"fluid":"immersiveengineering:creosote","amount":250}}