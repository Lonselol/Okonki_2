package com.example.paint2

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class FaceWindow {
    private var root = AnchorPane()
    private val width = 1600.0
    private val height = 900.0
    private var scene = Scene(root, width, height)
    private val list = createButtons()
    fun start(): Scene {
        root.onMouseClicked = EventHandler { mouseEvent ->
            list.layoutX = mouseEvent.x
            list.layoutY = mouseEvent.y
                if (root.children.any { it == list }) {
                    root.children.remove(list)
                } else {
                    root.children.add(list)
                }
        }
        root.style = "-fx-background-color: linear-gradient(to right, #8360c3, #2ebf91)"
        val end = EndNode()
        end.layoutX = width - end.rootPane!!.prefWidth - 30
        end.layoutY = height / 16
        root.children.add(end)

        return scene
    }

    private fun getStringNode(buttonTypes: ButtonTypes): String {
        val string = when (buttonTypes) {
            ButtonTypes.INT -> "Int"
            ButtonTypes.FLOAT -> "Float"
            ButtonTypes.STRING -> "String"
            ButtonTypes.IMAGE -> "Image"
            ButtonTypes.SEPIA -> "Sepia"
            ButtonTypes.GREY -> "Gray Filter"
            ButtonTypes.INVERT -> "Invert Filter"
            ButtonTypes.BRIGHT -> "Brightness"
            ButtonTypes.GAUSSIAN -> "Blur Filter"
            ButtonTypes.SCALE -> "Scale"
            ButtonTypes.MOVE -> "Move"
            ButtonTypes.ROTATE -> "Rotate"
        }
        return string
    }

    private fun createButtons(): VBox {
        val vBox = VBox(5.0)

        vBox.style = "-fx-padding: 20px 10px; -fx-background-color: #2ebf91; -fx-background-radius: 15px; -fx-border-style: solid; -fx-border-radius: 10px"

        fun createButton(buttonTypes: ButtonTypes) {
            val string = getStringNode(buttonTypes)
            val button = Button(string)
            button.style = "-fx-padding: 5px 10px; -fx-margin: 10px;" +
                    " -fx-text-style: bold; -fx-background-color: #708090; -fx-text-fill: #FFFFFF"
            button.onAction = EventHandler {
                val node = getNode(buttonTypes)
                node.layoutX = list.layoutX
                node.layoutY = list.layoutY
                root.children.add(node)
                if (root.children.any { it == list }) {
                    root.children.remove(list)
                }
            }
            vBox.children.add(button)
        }

        val buttonTypes: Array<ButtonTypes> = arrayOf(
            ButtonTypes.IMAGE, ButtonTypes.INT, ButtonTypes.FLOAT,
            ButtonTypes.STRING, ButtonTypes.SEPIA, ButtonTypes.GREY,
            ButtonTypes.INVERT, ButtonTypes.BRIGHT, ButtonTypes.GAUSSIAN,
            ButtonTypes.ROTATE, ButtonTypes.SCALE, ButtonTypes.MOVE
        )

        for (button in buttonTypes) {
            createButton(button)
        }

        return vBox
    }

    private fun getNode(buttonTypes: ButtonTypes): DraggableNode {
        return when (getStringNode(buttonTypes)) {
            "Int", IntNode::class.simpleName -> IntNode()
            "Float", FloatNode::class.simpleName -> FloatNode()
            "String", StringNode::class.simpleName -> StringNode()
            "Image", ImageNodeClass::class.simpleName -> ImageNodeClass()
            "Sepia", SepiaNode::class.simpleName -> SepiaNode()
            "Gray Filter", GreyNode::class.simpleName -> GreyNode()
            "Invert Filter", InvertNode::class.simpleName -> InvertNode()
            "Brightness", BrightnessNode::class.simpleName -> BrightnessNode()
            "Blur Filter", GaussianNode::class.simpleName -> GaussianNode()
            "Scale", ScalePercentNode::class.simpleName -> ScalePercentNode()
            "Move", MovePercentNode::class.simpleName -> MovePercentNode()
            "Rotate", RotateNode::class.simpleName -> RotateNode()
            EndNode::class.simpleName -> EndNode()
            else -> FloatNode()
        }
    }
}

class Paint2 : Application() {
    override fun start(primaryStage: Stage) {
        nu.pattern.OpenCV.loadLocally()
        primaryStage.scene = FaceWindow().start()
        primaryStage.title = "Paint 2"
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Paint2::class.java)
        }
    }
}