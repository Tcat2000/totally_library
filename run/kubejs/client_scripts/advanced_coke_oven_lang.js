ClientEvents.lang("en_us", event => {
    event.add("block.tlib.advanced_coke_oven", "Improved Coke Oven");
})

TLEvents.addManualEntries(event => {
    var page = event.builder();
    page.setContent("Improved Coke Oven", "Electrifying old tech", "The Improved Coke Oven does the same thing as the basic Coke Oven, but faster, with I/O, and can be sped up with power.\nWith coal or wood being inputed in the top, creosote comes out the bottom, charcoal or coke out the front, and power in the back.");
    page.addSpecialElement(event.specialElementData("advanced_coke_oven", 0, () => event.manualElementMultiblockId("totally_lib:advanced_coke_oven")));
    page.addSpecialElement(event.specialElementData("test", 1, () => event.manualElementLdlib(0, (manualScreen, x, y, buttons) => {
        var root = new WidgetGroup(100,100);
        var wid = new ImageWidget();
        wid.initTemplate();
        root.addWidget(wid);
        return root;
    })));
    // page.readFromFile("totally_lib:advanced_coke_oven");
    event.category("immersiveengineering:early_machines").addPage(page.create());
})