package dev.tbm00.spigot.data64.process;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import me.clip.placeholderapi.PlaceholderAPI;

import xzot1k.plugins.ds.api.objects.Shop;

import com.olziedev.playerwarps.api.warp.Warp;
import com.olziedev.playerwarps.api.player.WPlayer;

import dev.tbm00.spigot.data64.Data64;
import dev.tbm00.spigot.data64.hook.GDHook;

public class TransferProcess {
    private Data64 javaPlugin;
    private CommandSender sender;
    private Player playerA, playerB;

    public TransferProcess(Data64 javaPlugin, CommandSender sender, Player playerA, Player playerB) {
        this.javaPlugin = javaPlugin;
        this.playerA = playerA;
        this.playerB = playerB;

        javaPlugin.sendMessage(sender, ChatColor.GREEN + "Transfer process for " + playerA.getName() + " -> " + playerB.getName());
        tPerms();
        tClaimBlocks();
        tClaims();
        tJobs();
        tEco();
        tInv();
        tEnder();
        tShops();
        tHomes();
        tWarps();
        javaPlugin.sendMessage(playerB, "&aYour rank/perms, inv, ec, pocket, bank, displayshops, claims, claim blocks, sethomes, warps, and job stats have been transferred from player \n" + playerA.getName());
        javaPlugin.sendMessage(playerA, "&aYour rank/perms, inv, ec, pocket, bank, displayshops, claims, claim blocks, sethomes, warps, and job stats have been transferred to player \n" + playerB.getName());
    }

    /**
     * Transfers permissions from the source player to the target player.
     *
     * @return {@code true} if the permission transfer process completes successfully, {@code false} otherwise.
     */
    private boolean tPerms() {
        String rankB = parsePH(playerB, "%luckperms_current_group_on_track_rank%");
        String rankA = parsePH(playerA, "%luckperms_current_group_on_track_rank%");

        javaPlugin.runCommand("lp user " + playerB.getName() + " permission unset group." + rankB);
        javaPlugin.runCommand("lp user " + playerA.getName() + " clone " + playerB.getName());
        javaPlugin.runCommand("lp user " + playerA.getName() + " clear");
        javaPlugin.runCommand("lp user " + playerB.getName() + " permission set mc.ranknotime true");
        
        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tPerms: " + playerB.getName() + " now rank: " + rankA);
        return true;
    }

    /**
     * Transfers claim blocks from the source player to the target player by combining their bonus blocks.
     *
     * @return {@code true} if claim blocks were successfully transferred, {@code false} otherwise.
     */
    private boolean tClaimBlocks() {
        String totalBlocksA = parsePH(playerA, "%griefdefender_player_blocks_total%");
        String totalBlocksB = parsePH(playerB, "%griefdefender_player_blocks_total%");
        
        Integer totalA, totalB;
        try {
            totalA = Integer.parseInt(totalBlocksA);
            totalB = Integer.parseInt(totalBlocksB);
        } catch (Exception e) {
            javaPlugin.log(ChatColor.RED, "Caught exception parsing claim blocks integer from placeholder: " + e.getMessage());
            return false;
        }

        int total = totalA + totalB;

        javaPlugin.runCommand("gd player adjustbonusblocks " + playerB.getName() + " " + total);
        javaPlugin.runCommand("gd player adjustbonusblocks " + playerA.getName() + " 0");
        javaPlugin.runCommand("gd player setaccruedblocks " + playerA.getName() + " 0");
        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tClaimBlocks: " + playerB.getName() + " + " + total + " bonus blocks");
        return true;
    }

    /**
     * Transfers claims from the source player to the target player.
     *
     * @return {@code true} if the claim transfer process completes successfully, {@code false} otherwise.
     */
    private boolean tClaims() {
        int amountA = GDHook.getClaimAmount(playerA),
            amountB = GDHook.getClaimAmount(playerB),
            initialSum = amountA + amountB;

        GDHook.transferClaims(playerA, playerB);

        amountA = GDHook.getClaimAmount(playerA);
        amountB = GDHook.getClaimAmount(playerB);
        int afterSum = amountA + amountB;

        boolean valid = (initialSum==afterSum) ? true : false;

        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tClaims: " + playerA.getName() + ":" + amountA + " -> " + playerB.getName() + ":" + amountB + " " + valid);
        return true;
    }

    /**
     * Transfers job levels from the source player to the target player for various jobs.
     *
     * @return {@code true} after updating job levels for all applicable jobs.
     */
    private boolean tJobs() {
        Integer lvl = 0;

        String lvlBrewer = parsePH(playerA, "%jobsr_user_jlevel_brewer%");
        if (!lvlBrewer.equals("0")) {
            lvl = Integer.parseInt(lvlBrewer);
            javaPlugin.runCommand("jobs lvl " + playerB.getName() + " Brewer set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tJobs Brewer: " + lvl);
            
        }
    
        String lvlDigger = parsePH(playerA, "%jobsr_user_jlevel_digger%");
        if (!lvlDigger.equals("0")) {
            lvl = Integer.parseInt(lvlDigger);
            javaPlugin.runCommand("jobs lvl " + playerB.getName() + " Digger set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tJobs Digger: " + lvl);
        }
    
        String lvlFarmer = parsePH(playerA, "%jobsr_user_jlevel_farmer%");
        if (!lvlFarmer.equals("0")) {
            lvl = Integer.parseInt(lvlFarmer);
            javaPlugin.runCommand("jobs lvl " + playerB.getName() + " Farmer set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tJobs Farmer: " + lvl);
        }
    
        String lvlFisherman = parsePH(playerA, "%jobsr_user_jlevel_fisherman%");
        if (!lvlFisherman.equals("0")) {
            lvl = Integer.parseInt(lvlFisherman);
            javaPlugin.runCommand("jobs lvl " + playerB.getName() + " Fisherman set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tJobs Fisherman: " + lvl);
        }

        String lvlHunter = parsePH(playerA, "%jobsr_user_jlevel_hunter%");
        if (!lvlHunter.equals("0")) {
            lvl = Integer.parseInt(lvlHunter);
            javaPlugin.runCommand("jobs lvl " + playerB.getName()+" Hunter set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tJobs Hunter: " + lvl);
        }
    
        String lvlMiner = parsePH(playerA, "%jobsr_user_jlevel_miner%");
        if (!lvlMiner.equals("0")) {
            lvl = Integer.parseInt(lvlMiner);
            javaPlugin.runCommand("jobs lvl " + playerB.getName() + " Miner set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tJobs Miner: " + lvl);
        }
    
        String lvlWoodcutter = parsePH(playerA, "%jobsr_user_jlevel_woodcutter%");
        if (!lvlWoodcutter.equals("0")) {
            lvl = Integer.parseInt(lvlWoodcutter);
            javaPlugin.runCommand("jobs lvl " + playerB.getName() + " Woodcutter set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tJobs Woodcutter: " + lvl);
        }
        
        javaPlugin.runCommand("jobs fireall " + playerA.getName());

        return true;
    }

    /**
     * Transfers economy balances from the source player to the target player.
     * Combines the pocket and bank balances of both players and updates the target player with the sum.
     *
     * @return {@code true} if the economy values were successfully parsed and transferred, {@code false} otherwise.
     */
    private boolean tEco() {
        String pocketStringA = parsePH(playerA, "%vault_eco_balance%");
        String bankStringA = parsePH(playerA, "%bankplus_balance_long%");
        String pocketStringB = parsePH(playerB, "%vault_eco_balance%");
        String bankStringB = parsePH(playerB, "%bankplus_balance_long%");

        Double pocketA, bankA, pocketB, bankB;
        try {
            pocketA = Double.parseDouble(pocketStringA);
            bankA = Double.parseDouble(bankStringA);
            pocketB = Double.parseDouble(pocketStringB);
            bankB = Double.parseDouble(bankStringB);
        } catch (Exception e) {
            javaPlugin.log(ChatColor.RED, "Caught exception parsing economy double from placeholder: " + e.getMessage());
            return false;
        }

        double pocket = pocketA + pocketB;
        double bank = bankA + bankB;

        int pocketInt = (int) Math.round(pocket);
        int bankInt = (int) Math.round(bank);

        javaPlugin.runCommand("eco set " + playerB.getName() + " " + pocketInt);
        javaPlugin.runCommand("bp set " + playerB.getName() + " " + bankInt);
        javaPlugin.runCommand("eco set " + playerA.getName() + " 0");
        javaPlugin.runCommand("bp set " + playerA.getName() + " 0");
        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tEco p" + playerB.getName() + " $" + pocketStringB + " -> $" + pocketInt);
        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tEco b" + playerB.getName() + " $" + bankStringB + " -> $" + bankInt);
        return true;
    }

    /**
     * Transfers inventory items from the source player's inventory to the target player using shulker boxes.
     *
     * @return {@code true} if the inventory items were successfully transferred, {@code false} if the source inventory was empty.
     */
    private boolean tInv() {
        PlayerInventory invA = playerA.getInventory();
        List<ItemStack> allItems = new ArrayList<>();

        // Collect armor slots
        for (ItemStack armor : invA.getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                allItems.add(armor);
            }
        }
        
        // Collect offhand
        ItemStack offhand = invA.getItemInOffHand();
        if (offhand != null && offhand.getType() != Material.AIR) {
            allItems.add(offhand);
        }
        
        // Collect inventory items
        for (ItemStack item : invA.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                allItems.add(item);
            }
        }

        int itemCount = allItems.size();
        if (allItems.isEmpty() || itemCount==0) {
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tInv " + playerA.getName() + " empty inv");   
            return false;
        } else {
            invA.clear();
            invA.setArmorContents(null);
            invA.setItemInOffHand(null);
        }
        
        // Create shulker boxes and distribute items
        int shulkerIndex = 1;
        while (!allItems.isEmpty()) {
            ItemStack shulker = createShulkerBox("Transferred Inv Items " + shulkerIndex, allItems);
            giveItem(playerB, shulker);
            shulkerIndex++;
        }

        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tInv " + shulkerIndex + " shulkers, " + itemCount + " itemstacks");
        return true;
    }


    /**
     * Transfers ender chest items from the source player to the target player using shulker boxes.
     *
     * @return {@code true} if the ender chest items were successfully transferred, {@code false} otherwise.
     */
    private boolean tEnder() {
        Inventory enderA = playerA.getEnderChest();
        List<ItemStack> allItems = new ArrayList<>();

        // Collect enderchest items
        for (ItemStack item : enderA.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                allItems.add(item);
            }
        }

        int itemCount = allItems.size();
        if (allItems.isEmpty() || itemCount==0) {
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tEnder " + playerA.getName() + " empty ender");   
            return false;
        } else enderA.clear();
        
        // Create shulker boxes and distribute items
        int shulkerIndex = 1;
        while (!allItems.isEmpty()) {
            ItemStack shulker = createShulkerBox("Transferred Ender Items " + shulkerIndex, allItems);
            giveItem(playerB, shulker);
            shulkerIndex++;
        }

        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tEnder " + shulkerIndex + " shulkers, " + itemCount + " itemstacks");
        return true;
    }

    /**
     * Transfers display shops from source player to target player.
     * 
     * @return true once transfer completes
     */
    private boolean tShops() {
        ConcurrentHashMap<String, Shop> dsMap = Data64.dsHook.getManager().getShopMap();

        UUID uuidA = playerA.getUniqueId(), uuidB = playerB.getUniqueId();

        int i = 0;
        for (Shop shop : dsMap.values()) {
            if (shop.getOwnerUniqueId()!=null && shop.getOwnerUniqueId().equals(uuidA)) {
                shop.setOwnerUniqueId(uuidB);
                ++i;
            }
        }

        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tShops: " + i);
        return true;
    }

    /**
     * Transfers essentials homes from source player to target player.
     * 
     * @return true once transfer completes
     */
    private boolean tHomes() {
        List<String> homesA = Data64.essHook.getUser(playerA).getHomes();

        if (homesA == null || homesA.isEmpty()) {
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tHomes: 0");
            return true;
        }

        int i = 0;
        List<String> homesToProcess = new ArrayList<>(homesA);
        for (String homeName : homesToProcess) {
            Location homeLocation = Data64.essHook.getUser(playerA).getHome(homeName);
            if (homeLocation != null) {
                Data64.essHook.getUser(playerB).setHome(homeName, homeLocation);
                ++i;
            }
        }

        for (String homeName : homesToProcess) {
            try {
                Data64.essHook.getUser(playerA).delHome(homeName);
            } catch (Exception e) {
                javaPlugin.log(ChatColor.RED, "tHomes: Error deleting home " + homeName + " from " + playerA.getName() + ": " + e.getMessage());
            }
        }

        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tHomes: " + i);
        return true;
    }

    /**
     * Transfers player warps from source player to target player.
     * 
     * @return true once transfer completes
     */
    private boolean tWarps() {
        List<Warp> warps = Data64.pwHook.getPlayerWarps(true, playerA);

        if (warps == null || warps.isEmpty()) {
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tWarps: 0");
            return true;
        }

        WPlayer wplayerB = Data64.pwHook.getWarpPlayer(playerB.getUniqueId());

        int i = 0;
        for (Warp warp : warps) {
            warp.setWarpPlayer(wplayerB);
            ++i;
        }

        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "tWarps: " + i);
        return true;
    }

    /**
     * Creates a shulker box containing items from the provided list.
     * 
     * @param name the display name for the shulker box
     * @param items the list of ItemStack objects to store in the shulker box; items are removed as they are added
     * @return the created shulker box ItemStack
     */
    private ItemStack createShulkerBox(String name, List<ItemStack> items) {
        // Create a new Shulker Box item
        ItemStack shulker = new ItemStack(Material.SHULKER_BOX);
        BlockStateMeta meta = (BlockStateMeta) shulker.getItemMeta();
        if (meta == null) return shulker;

        ShulkerBox shulkerBlock = (ShulkerBox) meta.getBlockState();
        Inventory shulkerInv = shulkerBlock.getInventory();
        
        // Fill the shulker
        for (int i = 0; i < 27 && !items.isEmpty(); i++) {
            shulkerInv.setItem(i, items.remove(0));
        }
        
        meta.setBlockState(shulkerBlock);
        meta.setDisplayName(name);
        
        shulker.setItemMeta(meta);
        return shulker;
    }

    /**
     * Gives a player an ItemStack.
     * If they have a full inv, it drops on the ground.
     * 
     * @param player the player to give to
     * @param item the item to give
     */
    public static void giveItem(Player player, ItemStack item) {
        if ((player.getInventory().firstEmpty() == -1)) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else player.getInventory().addItem(item);
    }

    /**
     * Parses target's placeholder a message to a target CommandSender.
     * 
     * @param player the player to parse for
     * @param placeholder the placeholder to parse
     * @return the String value of the player's placeholder
     */
    private String parsePH(Player player, String placeholder) {
        return PlaceholderAPI.setPlaceholders(player, placeholder);
    }
}