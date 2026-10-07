package net.mokai.quicksandrehydrated.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.quicksands.DeepMudBlock;
import net.mokai.quicksandrehydrated.block.quicksands.MossyPeatBog;
import net.mokai.quicksandrehydrated.block.quicksands.Quickrug;
import net.mokai.quicksandrehydrated.block.quicksands.Quicksand;
import net.mokai.quicksandrehydrated.block.quicksands.core.FlowingQuicksandBase;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBase;
import net.mokai.quicksandrehydrated.block.quicksands.core.QuicksandBehavior;
import net.mokai.quicksandrehydrated.entity.data.QuicksandWobbleMEffect;
import net.mokai.quicksandrehydrated.entity.data.QuicksandWobblePEffect;
import net.mokai.quicksandrehydrated.util.BodyDepthThreshold;
import net.mokai.quicksandrehydrated.util.DepthCurve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static net.mokai.quicksandrehydrated.util.DepthCurve.Vec2;

public class QuicksandRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, QuicksandRehydrated.MOD_ID);

    /**
     *  ----- HOW TO ADD NEW SUBSTANCES ----
     *
     *  1. Assemble the Blockmodel.
     *    - Within the [resources/assets/qsrehydrated] folder, look at [blockstates] and [models/block] for examples of how to make a Blockmodel.
     *    - the resulting blockstate.json should have the 'canonical name', or the name Forge will look for when finding a Blockmodel.
     *  2. Create the RegistryObject<Block>.
     *    - This is the object that is fed to Forge in order to register the block.
     *    - Examples around Line 180. Feel free to copypaste one and rename the RegistryObject and string (the canonical name).
     *    -    public static final RegistryObject<Block> SAMPLE_QUICKSAND = registerBlock("sample_quicksand_canonical_name", () -> new QuicksandBase());
     *    - Instantiate a new QuicksandBase or relevant quicksand type, and include a BlockBehavior.Properties and a QuicksandBehavior.
     *
     *
     */
    // --------------------------------- Block properties -----------------------------

    private final static BlockBehaviour.Properties baseBlockBehavior = copyBlockProps(Blocks.SAND).requiresCorrectToolForDrops();
    private final static BlockBehaviour.Properties muddyBlockBehavior = copyBlockProps(Blocks.MUD).requiresCorrectToolForDrops();
    private final static BlockBehaviour.Properties baseFlowingBlockBehavior = copyBlockProps(Blocks.SAND).requiresCorrectToolForDrops();
    private final static BlockBehaviour.Properties slimeBlockBehavior = copyBlockProps(Blocks.SLIME_BLOCK).friction(1.0F).strength(2.5F);
    private final static BlockBehaviour.Properties woolBlockBehavior = copyBlockProps(Blocks.WHITE_WOOL).friction(1.0F).strength(2.5F);

    private static BlockBehaviour.Properties copyBlockProps(Block block) {
        return BlockBehaviour.Properties.copy(block).noCollission().forceSolidOn().isViewBlocking((A, B, C) -> true);
    }

    // --------------------------------- Quicksand properties -----------------------------

    static QuicksandBehavior MudBehavior = new QuicksandBehavior()
            .setVertSpeed(0.4)
            .setWalkSpeed(new DepthCurve(new double[]{0.9, 0.55, 0.15, 0.1, 0.0}))
            .setSinkSpeed(0)
            .setBuoyancyPoint(BodyDepthThreshold.KNEE.depth) // Buoyancy value for the knees
            .setCoverageTexture("mud_coverage")
            .setResurfingForce(0.025); // Moderate resurfing force for mud


    // Not that deep, not designed to get stuck in but shoud be a slog to walk through
    static QuicksandBehavior tidalMudBehaviour = new QuicksandBehavior()
        .setVertSpeed(0.3)
        .setWalkSpeed(new ArrayList<>(List.of(
            Vec2(0.9, 0.0),
            Vec2(0.9, BodyDepthThreshold.KNEE.depth),
            // Jumping in will make you sink further than the bouyancy depth and that gets you stuck
            Vec2(0.0, BodyDepthThreshold.WAIST.depth)
        )))
        .setSinkSpeed(0)
        .setBuoyancyPoint(BodyDepthThreshold.FEET.depth) // Thought it would be KNEE but this gives the visual look I want?
        // Not the same colour as the tidal_mud texture, but IRL the mud under the surface is often a darker colour
        // If someone adds footprints / surface disturbance effects to this block to match it'd look great
        .setCoverageTexture("mud_coverage")
        .setResurfingForce(0.03);


    static QuicksandBehavior quickrugSinkable = new QuicksandBehavior()

            .setVertSpeed(new ArrayList<>(List.of(Vec2(0.3, 0.0), Vec2(0.1, 0.35/2.0), Vec2(0.05, 1.0/2.0)
            )))

            .setSinkSpeed(new ArrayList<>(List.of(
                    Vec2(0.002, 0.0),
                    Vec2(0.0014, 0.3/2.0),
                    Vec2(0.0003, 0.4/2.0),
                    Vec2(0.0003, 1.0/2.0)
            )))

            .setWalkSpeed(new ArrayList<>(List.of(
                    Vec2(1.0, 0.0),
                    Vec2(0.9, 0.35/2.0),
                    Vec2(0.1, 0.5/2.0),
                    Vec2(0.0, 1.0/2.0)
            )))

            .setWobbleMove(0.9d)
            .setWobbleDecay(0.94d)
            .setWobbleRebound(0.4d/20.0d)
            .setWobbleApply(-0.5d/20.0d)

            .setStepOutHeight(BodyDepthThreshold.FEET.depth)
            .setBuoyancyPoint(BodyDepthThreshold.FEET.depth) // Buoyancy value for the ankles
            .setResurfingForce(0.06) // High resurfing force for quickrug

            .addQuicksandEffect(QuicksandWobbleMEffect.class);






    // ----------------------------------- QUICKSAND REGISTRY :O :O :O -----------------------------

    //When making new substances, make sure to include the new substance in resources/data/qsrehydrated/tags/blocks/quicksand_drownable.json.



    public static final RegistryObject<Block> QUICKSAND = registerBlock("quicksand", () -> new Quicksand( baseBlockBehavior.randomTicks(), new QuicksandBehavior()
            .setCoverageTexture("quicksand_coverage")
            .setSinkSpeed(.0005d)
            .setVertSpeed(.1d)
            .setWalkSpeed(new DepthCurve(0.9, 0.1))
            .setBuoyancyPoint(BodyDepthThreshold.KNEE.depth) // Buoyancy value for the knees (derived from CHEST)
            .setResurfingForce(0.03) // Default resurfing force
    ));

    public static final RegistryObject<Block> LIVING_SLIME = registerBlock("living_slime", () -> new QuicksandBase( slimeBlockBehavior, new QuicksandBehavior()
            .setSinkSpeed(0.001d)
            .setWalkSpeed(new DepthCurve(new double[]{1d, 0.75d}))
            .setVertSpeed(0.85d)
            .setWobbleMove(new DepthCurve(new double[]{0.01d, 0.001d}))
            .setWobbleTugHorizontal(0.1)
            .setWobbleTugVertical(0.1)
            .setBuoyancyPoint(BodyDepthThreshold.KNEE.depth) // Buoyancy value for the knees (calculated from WAIST)
            .setResurfingForce(0.05) // Higher resurfing force for slime

            .addQuicksandEffect(QuicksandWobblePEffect.class)
            .setCoverageTexture("slime_coverage")
    ));






    public static final RegistryObject<Block> MOSSY_PEAT_BOG = registerBlock("mossy_peat_bog", () -> new MossyPeatBog(muddyBlockBehavior, new QuicksandBehavior()
            .setSinkSpeed(new DepthCurve(new double[]{0.008, 0, -0.003, -0.003}))
            .setVertSpeed(.1)
            .setWalkSpeed(.4)

            .setWobbleMove(.6)
            .setWobbleTugHorizontal(.4)
            .setWobbleTugVertical(0)
            .setCoverageTexture("peat_bog_coverage")
            .setResurfingForce(0.02) // Lower resurfing force for bog

            .setBuoyancyPoint(BodyDepthThreshold.KNEE.depth) // Buoyancy value for the knees
    ));

    public static final RegistryObject<Block> PEAT_BOG = registerBlock("peat_bog", () -> new MossyPeatBog(muddyBlockBehavior, new QuicksandBehavior()
            .setSinkSpeed(new DepthCurve(new double[]{0.008, 0.006, 0.0045, 0.0030, 0.0015, 0.003, 0.001}))
            .setVertSpeed(.08)
            .setWalkSpeed(.4)

            .setWobbleMove(.6)
            .setWobbleTugHorizontal(.4)
            .setBuoyancyPoint(BodyDepthThreshold.KNEE.depth) // Buoyancy value for the knees (reduced from the upper thigh)
            .setResurfingForce(0.02) // Lower resurfing force for bog

            .setBubbleChance(.9)
            .setCoverageTexture("peat_bog_coverage")
    ));




    public static final RegistryObject<Block> BOG = registerBlock("bog", () -> new QuicksandBase(muddyBlockBehavior, new QuicksandBehavior()
            .setBuoyancyPoint(BodyDepthThreshold.ABDOMEN.depth) // Buoyancy value for the knees (calculated from WAIST)
            .setResurfingForce(0.005) // Lower resurfing force for bog
            .setCoverageTexture("bog_coverage")
    ));


    public static final RegistryObject<Block> THIN_MUD = registerBlock("thin_mud", () -> new DeepMudBlock( muddyBlockBehavior.sound(SoundType.MUD), MudBehavior, 0.2d));
    public static final RegistryObject<Block> SHALLOW_MUD = registerBlock("shallow_mud", () -> new DeepMudBlock( muddyBlockBehavior.sound(SoundType.MUD), MudBehavior, 0.5d));
    public static final RegistryObject<Block> DEEP_MUD = registerBlock("deep_mud", () -> new DeepMudBlock( muddyBlockBehavior.sound(SoundType.MUD), MudBehavior, 1.0d));
    public static final RegistryObject<Block> BOTTOMLESS_MUD = registerBlock("bottomless_mud", () -> new DeepMudBlock( muddyBlockBehavior.sound(SoundType.MUD), MudBehavior, 2.5d));

    public static final RegistryObject<Block> TIDAL_MUD = registerBlock("tidal_mud", () -> new QuicksandBase( muddyBlockBehavior.sound(SoundType.MUD), tidalMudBehaviour));

    public static final RegistryObject<Block> MIRE = registerBlock("mire", () -> new DeepMudBlock( muddyBlockBehavior.sound(SoundType.MUD), MudBehavior, 1.0d));
    public static final RegistryObject<Block> MARSH = registerBlock("marsh", () -> new DeepMudBlock( muddyBlockBehavior.sound(SoundType.MUD), MudBehavior, 1.0d));

    public static final RegistryObject<Block> SOFT_QUICKSAND = registerBlock("soft_quicksand", () -> new FlowingQuicksandBase(baseFlowingBlockBehavior, new QuicksandBehavior()
            .setBuoyancyPoint(BodyDepthThreshold.ABDOMEN.depth) // Buoyancy value for the knees (derived from SHOULDERS)
            .setResurfingForce(0.04) // Higher resurfing force for soft quicksand
    ));

    public static final RegistryObject<Block> WHITE_QUICKRUG = registerBlock("white_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.SNOW), quickrugSinkable));
    public static final RegistryObject<Block> ORANGE_QUICKRUG = registerBlock("orange_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_ORANGE), quickrugSinkable));
    public static final RegistryObject<Block> MAGENTA_QUICKRUG = registerBlock("magenta_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_MAGENTA), quickrugSinkable));
    public static final RegistryObject<Block> LIGHT_BLUE_QUICKRUG = registerBlock("light_blue_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_LIGHT_BLUE), quickrugSinkable));
    public static final RegistryObject<Block> YELLOW_QUICKRUG = registerBlock("yellow_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_YELLOW ), quickrugSinkable));
    public static final RegistryObject<Block> LIME_QUICKRUG = registerBlock("lime_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_LIGHT_GREEN), quickrugSinkable));
    public static final RegistryObject<Block> PINK_QUICKRUG = registerBlock("pink_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_PINK), quickrugSinkable));
    public static final RegistryObject<Block> GRAY_QUICKRUG = registerBlock("gray_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_GRAY), quickrugSinkable));
    public static final RegistryObject<Block> LIGHT_GRAY_QUICKRUG = registerBlock("light_gray_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_LIGHT_GRAY), quickrugSinkable));
    public static final RegistryObject<Block> CYAN_QUICKRUG = registerBlock("cyan_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_CYAN), quickrugSinkable));
    public static final RegistryObject<Block> PURPLE_QUICKRUG = registerBlock("purple_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_PURPLE), quickrugSinkable));
    public static final RegistryObject<Block> BLUE_QUICKRUG = registerBlock("blue_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_BLUE), quickrugSinkable));
    public static final RegistryObject<Block> BROWN_QUICKRUG = registerBlock("brown_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_BROWN), quickrugSinkable));
    public static final RegistryObject<Block> GREEN_QUICKRUG = registerBlock("green_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_GREEN), quickrugSinkable));
    public static final RegistryObject<Block> RED_QUICKRUG = registerBlock("red_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_RED), quickrugSinkable));
    public static final RegistryObject<Block> BLACK_QUICKRUG = registerBlock("black_quickrug", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_BLACK), quickrugSinkable));

    public static final RegistryObject<Block> WHITE_WETCRETE = registerBlock("white_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.SNOW), quickrugSinkable));
    public static final RegistryObject<Block> ORANGE_WETCRETE = registerBlock("orange_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_ORANGE), quickrugSinkable));
    public static final RegistryObject<Block> MAGENTA_WETCRETE = registerBlock("magenta_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_MAGENTA), quickrugSinkable));
    public static final RegistryObject<Block> LIGHT_BLUE_WETCRETE = registerBlock("light_blue_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_LIGHT_BLUE), quickrugSinkable));
    public static final RegistryObject<Block> YELLOW_WETCRETE = registerBlock("yellow_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_YELLOW ), quickrugSinkable));
    public static final RegistryObject<Block> LIME_WETCRETE = registerBlock("lime_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_LIGHT_GREEN), quickrugSinkable));
    public static final RegistryObject<Block> PINK_WETCRETE = registerBlock("pink_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_PINK), quickrugSinkable));
    public static final RegistryObject<Block> GRAY_WETCRETE = registerBlock("gray_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_GRAY), quickrugSinkable));
    public static final RegistryObject<Block> LIGHT_GRAY_WETCRETE = registerBlock("light_gray_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_LIGHT_GRAY), quickrugSinkable));
    public static final RegistryObject<Block> CYAN_WETCRETE = registerBlock("cyan_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_CYAN), quickrugSinkable));
    public static final RegistryObject<Block> PURPLE_WETCRETE = registerBlock("purple_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_PURPLE), quickrugSinkable));
    public static final RegistryObject<Block> BLUE_WETCRETE = registerBlock("blue_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_BLUE), quickrugSinkable));
    public static final RegistryObject<Block> BROWN_WETCRETE = registerBlock("brown_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_BROWN), quickrugSinkable));
    public static final RegistryObject<Block> GREEN_WETCRETE = registerBlock("green_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_GREEN), quickrugSinkable));
    public static final RegistryObject<Block> RED_WETCRETE = registerBlock("red_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_RED), quickrugSinkable));
    public static final RegistryObject<Block> BLACK_WETCRETE = registerBlock("black_wetcrete", () -> new Quickrug( woolBlockBehavior.mapColor(MapColor.COLOR_BLACK), quickrugSinkable));



    public static Collection<ItemStack> setupCreativeGroups() {
        CREATIVELIST = new ArrayList<>();
        addItem(QUICKSAND);
        addItem(LIVING_SLIME);
        addItem(SOFT_QUICKSAND);
        addItem(BOG);
        addItem(PEAT_BOG);
        addItem(MOSSY_PEAT_BOG);
        addItem(THIN_MUD);
        addItem(SHALLOW_MUD);
        addItem(DEEP_MUD);
        addItem(BOTTOMLESS_MUD);
        addItem(TIDAL_MUD);
        addItem(MIRE);
        addItem(MARSH);

        addItem(WHITE_QUICKRUG);
        addItem(ORANGE_QUICKRUG);
        addItem(MAGENTA_QUICKRUG);
        addItem(LIGHT_BLUE_QUICKRUG);
        addItem(YELLOW_QUICKRUG);
        addItem(LIME_QUICKRUG);
        addItem(PINK_QUICKRUG);
        addItem(GRAY_QUICKRUG);
        addItem(LIGHT_GRAY_QUICKRUG);
        addItem(CYAN_QUICKRUG);
        addItem(PURPLE_QUICKRUG);
        addItem(BLUE_QUICKRUG);
        addItem(BROWN_QUICKRUG);
        addItem(GREEN_QUICKRUG);
        addItem(RED_QUICKRUG);
        addItem(BLACK_QUICKRUG);

        addItem(WHITE_WETCRETE);
        addItem(ORANGE_WETCRETE);
        addItem(MAGENTA_WETCRETE);
        addItem(LIGHT_BLUE_WETCRETE);
        addItem(YELLOW_WETCRETE);
        addItem(LIME_WETCRETE);
        addItem(PINK_WETCRETE);
        addItem(GRAY_WETCRETE);
        addItem(LIGHT_GRAY_WETCRETE);
        addItem(CYAN_WETCRETE);
        addItem(PURPLE_WETCRETE);
        addItem(BLUE_WETCRETE);
        addItem(BROWN_WETCRETE);
        addItem(GREEN_WETCRETE);
        addItem(RED_WETCRETE);
        addItem(BLACK_WETCRETE);
        return CREATIVELIST;
    }





    // ----------------------------------- Done! -----------------------------


    public static Collection<ItemStack> CREATIVELIST;

    public static void addItem(RegistryObject<Block> b) {CREATIVELIST.add(b.get().asItem().getDefaultInstance());}

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
