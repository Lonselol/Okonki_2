package com.example.paint2

import javafx.scene.image.Image
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.ByteArrayInputStream

enum class NodeTypes {
    INT, FLOAT, STRING, IMAGE, NONE
}

enum class ButtonTypes {
    INT, FLOAT, STRING, IMAGE, SEPIA, GREY, INVERT,
    BRIGHT, GAUSSIAN, SCALE, MOVE, ROTATE
}

class Colors {
    companion object {
        const val RED = "#FA8072"
        const val BLACK = "#ADFF2F"
        const val GREEN = "#008000"
    }
}

class Formats {
    companion object {
        const val PNG = "*.png"
        const val JPG = "*.jpg"
    }
}

class Link {
    companion object {
        const val ONE = "firstLink"
        const val TWO = "secondLink"
        const val THREE = "thirdLink"
    }
}

class Titles {
    companion object {
        const val INT = "Int"
        const val INT_DEF = "0"
        const val FLOAT = "Float"
        const val FLOAT_DEF = "0.0"
        const val FLOAT_X = "float x"
        const val FLOAT_Y = "float y"
        const val STRING = "String"
        const val STRING_DEF = ""
        const val SEPIA = "Sepia"
        const val IMAGE = "Image"
        const val INVERT = "Invert"
        const val GREY = "Gray"
        const val BRIGHT = "Bright"
        const val GAUSSIAN = "Blur"
        const val SCALE = "Scale"
        const val MOVE = "Move"
        const val ROTATE = "Rotate"
        const val IMAGE_FILES = "Image Files"
        const val OPEN_IMAGE_FILE = "Open Image File"
        const val SAVE_IMAGE = "Save Image"
    }
}

class UIFXML {
    companion object {
        const val FINISH_NODE = "EndNode.fxml"
        const val IMAGE_NODE = "ImageNode.fxml"
        const val VALUE_NODE = "ValueNode.fxml"
        const val MODIFIER_NODE = "LinksNode.fxml"
        const val LINK_NODE = "NodeLink.fxml"
    }
}

class Config {
    companion object {
        fun matToImage(mat: Mat): Image {
            val buffer = MatOfByte()
            Imgcodecs.imencode(".png", mat, buffer)

            return Image(ByteArrayInputStream(buffer.toArray()))
        }
    }
}