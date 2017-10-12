# MineFana

## Description

Sends Minecraft statistics to a InfluxDB to be displayed by a Grafana instance.

## Stats

| Feature                       | Spigot    | BungeeCord    | Sponge    |
| :---------------------------: | :-------: | :-----------: | :-------: |
| Online players / max players  | X         | X             | X         |
| Average player ping           | X         | X             | X         |
| Player Country                | X         | X             | X         |
| Player Locale                 | X         | X             | X         |
| Player protocol version       | X         | X             |           |
| TPS (ticks per second)        | X         |               | X         |
| New players                   | X         |               | X         |
| Players, chunks, entities, tile entities per world | X | | X          |
| Players per server            |           | X             |           |
| Forge mods                    |           | X             |           |

## Compiling

1. Install gradle from here: https://gradle.org/install
2. Run `gradle shadow`
3. Look inside the folder build for of your target platform (Sponge, Bukkit, BungeeCord) and choose the *-all as the
target plugin.

## Screenshots

Example from the Grafana repository

![grafana dashboard](https://grafana.org/assets/img/features/dashboard_ex1.png)

## Credits

This product includes GeoLite2 data created by MaxMind, available from
<a href="https://www.maxmind.com">https://www.maxmind.com</a>.
