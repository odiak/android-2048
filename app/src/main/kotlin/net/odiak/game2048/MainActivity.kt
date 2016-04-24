package net.odiak.game2048

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

class MainActivity : AppCompatActivity() {

    companion object {
        val ID_LOGO = 1
        val ID_RESTART_BUTTON = 2
    }

    private val game = Game(4, 4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        relativeLayout {
            lparams {
                width = matchParent
                height = matchParent
            }
            padding = dip(8)

            textView("2048") {
                id = ID_LOGO
                textSize = 32f
            }

            button("restart") {
                id = ID_RESTART_BUTTON

                lparams {
                    below(ID_LOGO)
                }
            }

            ankoView({ BoardView(it, game) }, {
                lparams {
                    width = matchParent
                    height = matchParent
                    below(ID_RESTART_BUTTON)
                }
            })
        }
    }
}
