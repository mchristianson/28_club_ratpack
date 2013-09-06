import com.smdtest.twentyeightclub.DAOModule
import com.smdtest.twentyeightclub.Player
import com.smdtest.twentyeightclub.TwentyEightClubService
import groovy.json.JsonSlurper

import static org.ratpackframework.groovy.RatpackScript.ratpack
import static org.ratpackframework.groovy.Template.groovyTemplate
import org.ccil.cowan.tagsoup.Parser

String ENCODING = "UTF-8"

ratpack {

    modules {
        register new DAOModule(new File('./config', 'Config.groovy'))
    }

    handlers { TwentyEightClubService service ->

        get(":week") {
            def week = new Integer(pathTokens.week ?: 1)
            def PARSER = new XmlSlurper(new Parser() )

            def today = new Date()
            def firstWeek = new GregorianCalendar(2013, Calendar.SEPTEMBER, 4).time
            def weekStart = firstWeek + (7 * (week-1))
            def weekEnd = weekStart + 7
            Boolean showLive = (today > weekStart && today < weekEnd)
            def purse =  [
                1: ['None',16,	80],
                2: ['None',16,	80],
                3: ['None',16,	80],
                4: ['Panthers, Packers',					15	,	75	],
                5: ['Vikings, Steelers, Bucs, Redskins',	14	,	70	],
                6: ['Falcons, Dolphins',					15	,	75	],
                7: ['Saints, Raiders',						15	,	75	],
                8: ['Ravens, Bears, Texans, Colts, Chargers, Titans',	13	,	65	],
                9: ['Cardinals, Broncos, Lions, Jags, Giants, 49ers',	13	,	65	],
                10: ['Browns, Chiefs, Pats, Jets',			14	,	70	],
                11: ['Cowboys, Rams',	                    15	,	75	],
                12: ['Bills, Bengals, Eagles, Seahawks',	14	,	70	],
                13: ['None',	16	,	80	],
                14: ['None',	16	,	80	],
                15: ['None',	16	,	80	],
                16: ['None',	16	,	80	],
                17: ['None',	16	,	80	]
            ]
            def url = "http://www.nfl.com/scores/2013/REG${week}"

            def scores = []

            new URL(url).withReader (ENCODING) { reader ->

                def document = PARSER.parse(reader)
                def teams = document.'**'.findAll{ it['@class'] == 'team-data'}
                teams.each { team ->
                    def teamName = team.'**'.find{it['@class'] == 'team-name'}.text()
                    def score = team.'**'.find{it['@class'] == 'total-score'}.text()
                    scores << [team: teamName, score: score]
                }
            }

            List<Player> players = service.listPlayers().collect { player ->
                player.weekScore =  scores.find { it.team == player.team.nickName}?.score ?: 'Bye'
                player
            }

            if (showLive) {
                def json = new JsonSlurper().parseText( new URL( 'http://www.nfl.com/liveupdate/scores/scores.json' ).text )

                json.each { gameJson ->
                    def game = gameJson.value
                    Player homePlayer = players.find { it.team.abbr == game.home.abbr}
                    Player awayPlayer = players.find { it.team.abbr == game.away.abbr}


                    homePlayer.weekScore = game.home.score.T
                    awayPlayer.weekScore = game.away.score.T

                    if (game.posteam == homePlayer.team.abbr) {
                        homePlayer.possession = true
                        homePlayer.redzone = game.redzone
                    } else if (game.posteam == awayPlayer.team.abbr) {
                        awayPlayer.possession = true
                        awayPlayer.redzone = game.redzone
                    }

                    def down = game.down
                    down = (down == 1) ? "1st" :
                        (down == 2) ? "2nd" :
                        (down == 3) ? "3rd" : down
                    def qtr = game.qtr
                    qtr = (qtr == '1') ? "1st" :
                        (qtr == '2') ? "2nd" :
                        (qtr == '3') ? "3rd" : qtr

                    def liveStatus = (qtr != 'Final') ? "${down} &amp; ${game.togo} ${game.posteam} ball on the ${game.yl}<br/>${game.clock} remaining in the ${qtr}" : 'Final'
                    homePlayer.liveStatus = liveStatus
                    awayPlayer.liveStatus = liveStatus
                    homePlayer.style = service.getStatus(homePlayer)
                    awayPlayer.style = service.getStatus(awayPlayer)

                }
            }
            render groovyTemplate("skin.html", week: week, players: players, purse: purse)
        }

        assets "public"
    }
}


