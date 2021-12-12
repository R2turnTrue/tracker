package xyz.r2turntrue.tracker.plugin

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class TrackerPlugin: JavaPlugin(), Listener {

    val track = HashMap<UUID, UUID>()

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        kommand {
            register("track") {
                then("target" to player()) {
                    executes { ctx ->
                        val target: Player by ctx
                        track[player.uniqueId] = target.uniqueId
                        player.compassTarget = target.location
                        target.sendMessage(Component.text("${player.name}님이 당신을 추적하기 시작했습니다!", NamedTextColor.GRAY))
                    }
                }
            }
        }
    }

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        if(!event.player.inventory.contains(Material.COMPASS)) {
            event.player.inventory.addItem(ItemStack(Material.COMPASS))
        }
        if(track.contains(event.player.uniqueId)) {
            val target = track[event.player.uniqueId]!!
            if(Bukkit.getPlayer(target) != null) {
                event.player.compassTarget = Bukkit.getPlayer(target)!!.location
            }
        }
    }

    @EventHandler
    fun drop(event: PlayerDropItemEvent) {
        if(event.itemDrop.itemStack.type == Material.COMPASS) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun move(event: PlayerMoveEvent) {
        for (entry in track) {
            if(entry.value == event.player.uniqueId) {
                val tracker = Bukkit.getPlayer(entry.key)
                if(tracker != null) {
                    tracker.compassTarget = event.player.location
                }
            }
        }
    }

}