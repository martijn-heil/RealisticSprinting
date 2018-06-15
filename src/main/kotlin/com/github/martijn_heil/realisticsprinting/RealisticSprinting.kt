/*
 *
 * RealisticSprinting
 * Copyright (C) 2018 Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.martijn_heil.realisticsprinting

import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class RealisticSprinting : JavaPlugin() {
    private val decreasePerSecondBaseValue = 2
    private val increasePerSecondBaseValue = 1
    private val playerMap = HashMap<UUID, Pair<Int /* increase */, Int /* decrease */>>()

    override fun onEnable() {
        if (server.scheduler.scheduleSyncRepeatingTask(this, {
                    server.onlinePlayers.forEach {
                        if (!it.isSprinting && it.foodLevel < 20) {
                            val increase = playerMap[it.uniqueId]?.first ?: increasePerSecondBaseValue
                            var newLevel = it.foodLevel + increase
                            if (newLevel > 20) newLevel = 20 // prevent overflow
                            it.foodLevel = newLevel
                        } else if (it.isSprinting && it.foodLevel > 0) {
                            val decrease = playerMap[it.uniqueId]?.second ?: decreasePerSecondBaseValue
                            var newLevel = it.foodLevel - decrease
                            if (newLevel < 0) newLevel = 0 // prevent underflow
                            it.foodLevel = newLevel
                        }
                    }
                }, 0, 20) == -1) {
            logger.severe("Could not schedule task."); this.isEnabled = false; return
        }
    }

    override fun onDisable() {
        server.scheduler.cancelTasks(this)
    }
}