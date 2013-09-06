package com.smdtest.twentyeightclub

/**
 * Created by mchristianson on 8/15/13.
 */
class Player {
    Long id
    String firstName
    String lastName
    String email
    String weekScore
    Team team
    Boolean redzone = Boolean.FALSE
    Boolean possession = Boolean.FALSE
    String liveStatus
    String style

    static Player createFromDb(values) {
        new Player(id: values.id?:null,
                firstName: values.first_name?:null,
                lastName: values.last_name?:null,
                email: values.email?:null,
                team:
                    new Team(
                            id: values.team_id?:null,
                            abbr: values.abbr?:null,
                            city: values.city?:null,
                            nickName: values.nickname?:null
                    )
        )
    }

}
