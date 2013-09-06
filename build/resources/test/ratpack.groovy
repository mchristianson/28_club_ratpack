import com.smdtest.twentyeightclub.DAOModule
import com.smdtest.twentyeightclub.Player
import com.smdtest.twentyeightclub.TwentyEightClubService

import static org.ratpackframework.groovy.RatpackScript.ratpack
import static org.ratpackframework.groovy.Template.groovyTemplate
import org.ccil.cowan.tagsoup.Parser;

String ENCODING = "UTF-8"

ratpack {

    modules {
        register new DAOModule(new File('./config', 'Config.groovy'))
    }

    handlers { TwentyEightClubService service ->

        get {
            def PARSER = new XmlSlurper(new Parser() )

            def url = "http://www.bing.com/search?q=web+scraping"

            new URL(url).withReader (ENCODING) { reader ->

                def document = PARSER.parse(reader)
                // Extracting information
            }
            //            render 'test'
            List players = service.listPlayers()
            render groovyTemplate("skin.html", players: players)
        }



        assets "public"
    }
}


