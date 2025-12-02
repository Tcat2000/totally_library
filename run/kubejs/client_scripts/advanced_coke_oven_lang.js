ClientEvents.lang("en_us", event => {
    event.add("block.tlib.advanced_coke_oven", "Improved Coke Oven");
})

IEMultiblockEvents.addManualEntries(event => {
    var page = event.builder();
    page.addSpecialElement(event.specialElementData("advanced_coke_oven", 0, () => event.manualElementMultiblockId("totally_lib:advanced_coke_oven")));
    page.readFromFile("totally_lib:advanced_coke_oven");
    event.category("immersiveengineering:early_machines").addLeaf(page.create());
})