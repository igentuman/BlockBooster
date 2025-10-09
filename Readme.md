# BlockBooster

BlockBooster is a Minecraft Forge mod that accelerates block entities (furnaces, machines, etc.) by placing them on top of booster blocks. The mod provides multiple tiers of boosters with different power sources and capabilities.

## Features

- **Multiple Booster Tiers**: Four different booster types with varying speeds and energy requirements
- **Energy-Based Operation**: Boosters require Forge Energy (FE) or Mana to operate
- **Configurable Performance**: Extensive configuration options for balancing and server performance
- **Redstone Control**: Optional redstone signal deactivation
- **Server-Friendly**: Built-in TPS protection and performance monitoring
- **Whitelist/Blacklist Support**: Control which block entities can be boosted

## Booster Types

### Tier 1 Booster
- **Boost Rate**: 2x speed (configurable)
- **Energy Cost**: 5,000 FE/tick (configurable)
- **Range**: Single block directly above
- **Recipe**: Iron Ingots + Clock + Emeralds + Redstone Dust

### Tier 2 Booster
- **Boost Rate**: 5x speed (configurable)
- **Energy Cost**: 10,000 FE/tick (configurable)
- **Range**: Single block directly above
- **Recipe**: Gold Ingots + Tier 1 Booster + Emeralds + Redstone Dust

### Tier 3 Booster
- **Boost Rate**: 10x speed (configurable)
- **Energy Cost**: 20,000 FE/tick (configurable)
- **Range**: Configurable radius (default 3 blocks, scans a cube area)
- **Recipe**: Diamonds + Tier 2 Booster + Emeralds + Redstone Dust
- **Special Feature**: Can boost multiple blocks within its scan radius

### Mana Booster (Requires Botania)
- **Boost Rate**: 5x speed (configurable)
- **Energy Cost**: 100 Mana/tick (configurable)
- **Range**: Single block directly above
- **Recipe**: Netherite Ingots + Tier 1 Booster + Emeralds + Redstone Dust
- **Special Feature**: Uses Botania's Mana instead of Forge Energy

## Configuration Options

All configuration options can be found in `config/blockbooster-common.toml`:

### General Settings
- **boosters_per_chunk**: Limit the number of boosters per chunk (default: 5, range: 1-20)
  - Helps prevent server performance issues

### Tier 1 Settings
- **t1_fe_per_tick**: Energy consumption per tick (default: 5000)
- **t1_boost_rate**: Speed multiplier (default: 2)

### Tier 2 Settings
- **t2_fe_per_tick**: Energy consumption per tick (default: 10000)
- **t2_boost_rate**: Speed multiplier (default: 5)

### Tier 3 Settings
- **t3_fe_per_tick**: Energy consumption per tick (default: 20000)
- **t3_boost_rate**: Speed multiplier (default: 10)
- **t3_scan_radius**: Cube scan radius for multi-block boosting (default: 3, range: 1-10)

### Mana Booster Settings
- **mana_per_tick**: Mana consumption per tick (default: 100)
- **mana_booster_rate**: Speed multiplier (default: 5)

### Control Settings
- **deactivate_with_redstone**: Disable booster when receiving redstone signal (default: true)

### Whitelist/Blacklist
- **white_list**: List of block entities that can be boosted (higher priority, empty by default)
  - Example: `["minecraft:furnace", "somemod:machine"]`
- **black_list**: List of block entities that cannot be boosted (default: `["mekanism:bounding_block"]`)
  - Example: `["minecraft:furnace", "somemod:machine"]`

### Performance Protection
- **enable_tps_protection**: Enable TPS-based lag protection (default: true)
  - Boosters automatically pause when server TPS drops below threshold
- **min_tps_threshold**: Minimum TPS for boosters to operate (default: 15.0, range: 1.0-20.0)
  - Boosters stop working if server TPS falls below this value

### Slow Block Prevention
- **prevent_slow_blocks**: Prevent boosting blocks that take too long to process (default: true)
- **slow_block_threshold_ns**: Threshold in nanoseconds for slow blocks (default: 5000000, range: 100000-100000000)
  - Blocks taking longer than this threshold will be marked as slow and won't be boosted
  - 1ms = 1,000,000ns, default is 5ms

## Usage

1. **Craft a Booster**: Use the recipes above to create your desired booster tier
2. **Place the Booster**: Place the booster block in your world
3. **Power the Booster**: Connect it to a Forge Energy source (or Mana for Mana Booster)
4. **Place Block Entity**: Place the block entity (furnace, machine, etc.) directly on top of the booster
   - For Tier 3, blocks within the scan radius will also be boosted
5. **Monitor Status**: Right-click the booster to open its GUI and check energy levels and status

## Requirements

- **Minecraft**: 1.20.1
- **Forge**: 1.20.1-47.2.0 or higher
- **Optional**: Botania (for Mana Booster)

## Building from Source

```bash
./gradlew build
```

The compiled mod will be in `build/libs/`

## Credits

Mod sources originally based on [TutorialV3 by McJty](https://github.com/McJty/TutorialV3)

## License

See LICENSE file for details.