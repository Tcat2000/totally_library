console.log("startup scripts loading!");

// IEMultiblockEvents.jeiCategory
// IEMultiblockEvents.manualEntry
IEMultiblockEvents.registerMultiblocks(event => {
    console.log("registering multiblocks")
    event.multiblock("totally_lib:advanced_coke_oven")
        .size(3,4,3)
        .masterOffset(1,1,1)
        .triggerOffset(1,1,2)
        .manualScale(16)
        .traits(() => [
            new TLEnergyTrait("power", 100000).expose(new BlockPos(1,0,0), TraitIOSides.FRONT),
            new TLItemTrait("input", 1).expose(new BlockPos(1,3,1), TraitIOSides.TOP),
            new TLItemTrait("buffer", 1),
            new TLItemTrait("output", 1).expose(new BlockPos(1,0,2), TraitIOSides.BACK).expose(new BlockPos(1,0,2), TraitIOSides.BOTTOM),
            new TLFluidTrait("input_tank", 10000, fluid => true).expose(new BlockPos(1,0,1), TraitIOSides.BOTTOM)
        ])
        .form([[-1.5,-1.0,-0.5],[-1.5,-1.0,0.5],[-1.5,-1.0,1.5],[-0.5,-1.0,-0.5],[-0.5,-1.0,0.5],[-0.5,-1.0,1.5],[0.5,-1.0,-0.5],[0.5,-1.0,0.5],[0.5,-1.0,1.5],[-1.5,0.0,-0.5],[-1.5,0.0,0.5],[-1.5,0.0,1.5],[-0.5,0.0,-0.5],[-0.5,0.0,0.5],[-0.5,0.0,1.5],[0.5,0.0,-0.5],[0.5,0.0,0.5],[0.5,0.0,1.5],[-1.5,1.0,-0.5],[-1.5,1.0,0.5],[-1.5,1.0,1.5],[-0.5,1.0,-0.5],[-0.5,1.0,0.5],[-0.5,1.0,1.5],[0.5,1.0,-0.5],[0.5,1.0,0.5],[0.5,1.0,1.5],[-1.5,2.0,-0.5],[-1.5,2.0,0.5],[-1.5,2.0,1.5],[-0.5,2.0,-0.5],[-0.5,2.0,1.5],[0.5,2.0,-0.5],[0.5,2.0,0.5],[0.5,2.0,1.5],[-0.5,2.0,0.5],])
        .obj("immersiveengineering:models/block/blastfurnace_advanced.obj", {particle: "immersiveengineering:block/multiblocks/blast_furnace_advanced"}, false, true)
        .shape([Shapes.offset(16.0,16.0,37.0,16.0,0.0,0.0),Shapes.offset(26.0,16.0,26.0,11.0,0.0,11.0),Shapes.offset(10.0,13.0,11.0,19.0,0.0,37.0),Shapes.size(10.0,16.0,10.0,38.0,19.0,38.0),Shapes.size(9.0,19.0,9.0,39.0,22.0,39.0),Shapes.size(8.0,22.0,8.0,40.0,27.0,40.0),Shapes.size(9.0,27.0,9.0,39.0,31.0,39.0),Shapes.size(10.0,31.0,10.0,38.0,35.0,38.0),Shapes.size(11.0,35.0,11.0,37.0,39.0,37.0),Shapes.size(12.0,39.0,12.0,36.0,43.0,36.0),Shapes.size(13.0,43.0,13.0,35.0,47.0,35.0),Shapes.size(14.0,47.0,14.0,34.0,51.0,34.0),Shapes.size(15.0,51.0,15.0,33.0,55.0,33.0),Shapes.size(16.0,55.0,16.0,32.0,64.0,32.0),Shapes.offset(48.0,16.0,16.0,0.0,0.0,16.0),Shapes.offset(16.0,6.0,8.0,16.0,16.0,2.0),])
        .recipe(builder => {
            builder.addProvider(new IntProvider("length"));
            builder.addProvider(new IngredientProvider("input"));
            builder.addProvider(new ItemStackProvider("output"));
            builder.addProvider(new FluidStackProvider("byproduct"));
            builder.addProvider(new IntProvider("power", 256));
            builder.length(process => {
                console.log(process);
                return process.get("length").get().get();
            });
            builder.processTick(0, (process, par) => {
                var inputInventory = process.state.getTrait("input").storage.getValue();
                var bufferInventory = process.state.getTrait("buffer").storage.getValue();
                if(process.getProvider("input").canExtractFrom(inputInventory.getStackInSlot(0))) {
                    bufferInventory.setStackInSlot(0, process.getProvider("input").extractFrom(inputInventory.getStackInSlot(0)));
                    process.stuck[0] = false;
                }
                else process.stuck[0] = true;
                return false;
            });
            builder.processTick(-1, (process, par) => {
                var outputInventory = process.state.getTrait("output").storage.getValue();
                var bufferInventory = process.state.getTrait("buffer").storage.getValue();
                
                if(process.getProvider("output").insertTo(outputInventory, 0)) {
                    bufferInventory.setStackInSlot(0, Items.AIR);
                    process.setWorking(true);
                    return true;
                }
                process.setWorking(false);
                return false;
            });
            builder.tickLogic((process, tick, parallel) => {
                var power = process.getTrait("power");
                var hasPower = power.attemptExtract(process.getProvider("power").get());
                return hasPower;
            });
            builder.executeCondition((recipe, state) => {
                var inputInventory = state.getTrait("input").storage.getValue();
                var outputInventory = state.getTrait("output").storage.getValue();
                return recipe.getProvider("input").canExtractFrom(inputInventory.getStackInSlot(0)) &&
                        recipe.getProvider("output").canInsertTo(outputInventory.getStackInSlot(0));
            });
        })
        .jeiCatalyst("immersiveengineering:coke_oven")
    .build();
});