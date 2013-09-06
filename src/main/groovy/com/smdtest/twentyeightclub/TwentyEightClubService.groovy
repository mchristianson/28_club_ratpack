package com.smdtest.twentyeightclub

import com.smdtest.twentyeightclub.Player
import groovy.sql.Sql

class TwentyEightClubService {

    private final Sql sql

    TwentyEightClubService(Sql sql) {
       this.sql = sql
    }

    List<Player> listPlayers() {
        List<Player> playerList = sql.rows('''
                SELECT p.*, t.id as team_id, t.nickname, t.city, t.abbr
                FROM player p
                join player_team_year py on py.player_id = p.id
                join team t on t.id = py.team_id
                ORDER BY last_name desc
            ''').collect { dbRow ->
            Player.createFromDb(dbRow)
        }
        playerList
    }

    Player getPlayer(Long id) {
        Player.createFromDb(sql.firstRow('SELECT * FROM player WHERE id = ?', [id]))
    }

    String getStatus(Player player) {
        Integer score = player.weekScore as Integer
        return score == 28 ? 'success' :
        score > 28 ? 'error' :
        player.redzone ? 'redzone' : ''
    }
}
