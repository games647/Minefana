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
| Players, chunks, entities, 
  tile entities per world       | X         |               | X         |
| Players per server            |           | X             |           |
| Forge mods                    |           | X             |           |

## Planned

* Unique new and returning users
* Session and daily play time
* Update changes only on Events

## Screenshots

Example from the Grafana repository

![grafana dashboard](https://grafana.org/assets/img/features/dashboard_ex1.png)

## Credits

This product includes GeoLite2 data created by MaxMind, available from
<a href="https://www.maxmind.com">https://www.maxmind.com</a>.
