package com.example.paint2

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.stage.FileChooser
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.IOException
import kotlin.math.roundToInt


abstract class ValueNode : DraggableNode() {
    @FXML
    var value: TextField? = null

    @FXML
    override var nodesCount = 1

    init {
        init(UIFXML.VALUE_NODE)
    }

    override fun updateNode() {
        if (getValue() != null) {
            (outputLinkHandle!!.children.find { it is Circle } as Circle).fill =
                Paint.valueOf(Colors.GREEN)
        } else {
            (outputLinkHandle!!.children.find { it is Circle } as Circle).fill =
                Paint.valueOf(Colors.RED)
        }
    }

    override fun toData(): NodeData {
        val data = super.toData()
        data.data = value!!.text
        return data
    }

    override fun fromData(nodeData: NodeData) {
        super.fromData(nodeData)
        value!!.text = nodeData.data
    }
}

class IntNode : ValueNode() {
    override val nodeType: NodeTypes = NodeTypes.INT
    override fun addInit() {
        value!!.text = Titles.INT_DEF
        titleBar!!.text = Titles.INT

        value!!.textProperty().addListener { _, _, _ ->
            updateNode()
            outputLink?.kickAction()
        }
    }

    override fun getValue(): Int? {
        return value!!.text.toIntOrNull()
    }

}

class FloatNode : ValueNode() {

    override val nodeType: NodeTypes = NodeTypes.FLOAT

    override fun addInit() {
        value!!.text = Titles.FLOAT_DEF
        titleBar!!.text = Titles.FLOAT

        value!!.textProperty().addListener { _, _, _ ->
            updateNode()
            outputLink?.kickAction()
        }
    }

    override fun getValue(): Float? {
        return value!!.text.toFloatOrNull()
    }
}

class StringNode : ValueNode() {
    override val nodeType: NodeTypes = NodeTypes.STRING
    override fun addInit() {
        value!!.text = Titles.STRING_DEF
        titleBar!!.text = Titles.STRING

        value!!.textProperty().addListener { _, _, _ ->
            updateNode()
            outputLink?.kickAction()
        }
    }

    override fun getValue(): String {
        return value!!.text
    }
}

abstract class ImageNode : DraggableNode() {
    override val nodeType: NodeTypes = NodeTypes.IMAGE

    @FXML
    var firstLink: AnchorPane? = null

    @FXML
    var secondLink: AnchorPane? = null

    @FXML
    var thirdLink: AnchorPane? = null

    @FXML
    var imageView: ImageView? = null

    override fun updateNode() {
        isRightNodes()
        val v = getValue() as Mat?
        if (v != null) {
            imageView!!.isVisible = true
            imageView!!.image = Config.matToImage(v)
            (outputLinkHandle!!.children.find { it is Circle } as Circle).fill =
                Paint.valueOf(Colors.GREEN)
        } else {
            (outputLinkHandle!!.children.find { it is Circle } as Circle).fill =
                Paint.valueOf(Colors.RED)
        }
    }
}

class SepiaNode : ImageNode() {

    @FXML
    override var nodesCount = 1
    override fun addInit() {
        titleBar!!.text = Titles.SEPIA

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
    }

    override fun getValue(): Mat? {
        val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)

        val colMat = Mat(3, 3, CvType.CV_64FC1)
        val row = 0
        val col = 0
        colMat.put(
            row, col, 0.272, 0.534, 0.131, 0.349, 0.686, 0.168, 0.393, 0.769, 0.189
        )

        val mat2 = Mat()
        mat.copyTo(mat2)
        Core.transform(mat, mat2, colMat)

        isRightNodes()
        return mat2
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }

}

class InvertNode : ImageNode() {

    @FXML
    override var nodesCount = 1
    override fun addInit() {
        titleBar!!.text = Titles.INVERT

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
    }

    override fun getValue(): Mat? {
        val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)

        val mat2 = Mat()
        mat.copyTo(mat2)
        Core.bitwise_not(mat, mat2)

        isRightNodes()
        return mat2
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }

}

class GreyNode : ImageNode() {

    @FXML
    override var nodesCount = 1
    override fun addInit() {
        titleBar!!.text = Titles.GREY

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
    }

    override fun getValue(): Mat? {
        val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)

        val mat2 = Mat()
        mat.copyTo(mat2)
        Imgproc.cvtColor(mat, mat2, Imgproc.COLOR_BGR2GRAY)

        val mat3 = Mat()

        Core.merge(List(3) { mat2 }, mat3)

        isRightNodes()
        return mat3
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }

}

class BrightnessNode : ImageNode() {
    @FXML
    override var nodesCount = 2

    override fun addInit() {
        titleBar!!.text = Titles.BRIGHT

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes[Link.TWO] = Triple(secondLink!!, null, NodeTypes.FLOAT)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
        (secondLink!!.children.find { it is Label } as Label).text = Titles.FLOAT
    }


    override fun getValue(): Mat? {
        fun saturate(`val`: Double): Byte {
            var iVal = `val`.roundToInt()
            iVal = if (iVal > 255) 255 else if (iVal < 0) 0 else iVal
            return iVal.toByte()
        }

        val image = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)
        val beta = nodes[Link.TWO]!!.second?.getValue() as Float? ?: return isErrorNode(Link.TWO)
        val alpha = 1.0

        val newImage = Mat()
        image.copyTo(newImage)

        val imageData = ByteArray(((image.total() * image.channels()).toInt()))
        image.get(0, 0, imageData)
        val newImageData = ByteArray((newImage.total() * newImage.channels()).toInt())
        for (y in 0 until image.rows()) {
            for (x in 0 until image.cols()) {
                for (c in 0 until image.channels()) {

                    var pixelValue =
                        imageData[(y * image.cols() + x) * image.channels() + c].toDouble()

                    pixelValue =
                        if (pixelValue < 0) pixelValue + 256
                        else pixelValue

                    newImageData[(y * image.cols() + x) * image.channels() + c] =
                        saturate(alpha * pixelValue + beta)

                }
            }
        }
        newImage.put(0, 0, newImageData)

        isRightNodes()
        return newImage
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }

}

class GaussianNode : ImageNode() {

    @FXML
    override var nodesCount = 2
    override fun addInit() {
        titleBar!!.text = Titles.GAUSSIAN

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes[Link.TWO] = Triple(secondLink!!, null, NodeTypes.INT)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
        (secondLink!!.children.find { it is Label } as Label).text = Titles.INT
    }


    override fun getValue(): Mat? {
        val image = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)
        var kernelSize = nodes[Link.TWO]!!.second?.getValue() as Int? ?: return isErrorNode(Link.TWO)

        kernelSize = kernelSize * 2 + 1
        if (kernelSize <= 0 || kernelSize > 100)
            return null


        val newImage = Mat()
        image.copyTo(newImage)

        Imgproc.GaussianBlur(image, newImage, Size(
            kernelSize.toDouble(), kernelSize.toDouble()
        ), 0.0)

        isRightNodes()
        return newImage
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }

}

class ScalePercentNode : ImageNode() {
    @FXML
    override var nodesCount = 3
    override fun addInit() {
        titleBar!!.text = Titles.SCALE

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes[Link.TWO] = Triple(secondLink!!, null, NodeTypes.FLOAT)
        nodes[Link.THREE] = Triple(thirdLink!!, null, NodeTypes.FLOAT)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
        (secondLink!!.children.find { it is Label } as Label).text = Titles.FLOAT_X
        (thirdLink!!.children.find { it is Label } as Label).text = Titles.FLOAT_Y
    }

    override fun getValue(): Mat? {
        val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)
        val px = nodes[Link.TWO]!!.second?.getValue() as Float? ?: return isErrorNode(Link.TWO)
        val py = nodes[Link.THREE]!!.second?.getValue() as Float? ?: return isErrorNode(Link.THREE)

        val x = mat.cols() * px / 100
        val y = mat.rows() * py / 100

        if (x <= 0 || y <= 0)
            return null

        val mat2 = Mat()
        mat.copyTo(mat2)
        Imgproc.resize(mat, mat2, Size(x.toDouble(), y.toDouble()))

        isRightNodes()
        return mat2
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }

}

class MovePercentNode : ImageNode() {
    @FXML
    override var nodesCount = 3
    override fun addInit() {
        titleBar!!.text = Titles.MOVE

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes[Link.TWO] = Triple(secondLink!!, null, NodeTypes.FLOAT)
        nodes[Link.THREE] = Triple(thirdLink!!, null, NodeTypes.FLOAT)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
        (secondLink!!.children.find { it is Label } as Label).text = Titles.FLOAT_X
        (thirdLink!!.children.find { it is Label } as Label).text = Titles.FLOAT_Y
    }

    override fun getValue(): Mat? {
        val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)
        val px = nodes[Link.TWO]!!.second?.getValue() as Float? ?: return isErrorNode(Link.TWO)
        val py = nodes[Link.THREE]!!.second?.getValue() as Float? ?: return isErrorNode(Link.THREE)

        val mat2 = Mat()
        mat.copyTo(mat2)

        val moveMat = Mat(2, 3, CvType.CV_64FC1)
        val row = 0
        val col = 0
        moveMat.put(
            row, col, 1.0, 0.0, (mat.cols() * px / 100.0), 0.0, 1.0, (mat.rows() * py / 100.0)
        )

        Imgproc.warpAffine(mat, mat2, moveMat, Size(mat.cols().toDouble(), mat.rows().toDouble()))

        isRightNodes()
        return mat2
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }

}

class RotateNode : ImageNode() {
    @FXML
    override var nodesCount = 2
    override fun addInit() {
        titleBar!!.text = Titles.ROTATE

        firstLink!!.isVisible = (nodesCount > 0)
        secondLink!!.isVisible = (nodesCount > 1)
        thirdLink!!.isVisible = (nodesCount > 2)

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)
        nodes[Link.TWO] = Triple(secondLink!!, null, NodeTypes.FLOAT)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE
        (secondLink!!.children.find { it is Label } as Label).text = Titles.FLOAT
    }

    override fun getValue(): Mat? {
        val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return isErrorNode(Link.ONE)
        val deg = nodes[Link.TWO]!!.second?.getValue() as Float? ?: return isErrorNode(Link.TWO)

        val mat2 = Mat()
        mat.copyTo(mat2)

        val rotMat = Imgproc.getRotationMatrix2D(Point(
            mat.cols() / 2.0, mat.rows() / 2.0), deg.toDouble(), 1.0)

        Imgproc.warpAffine(mat, mat2, rotMat, Size(mat.cols().toDouble(), mat.rows().toDouble()))

        isRightNodes()
        return mat2
    }

    init {
        init(UIFXML.MODIFIER_NODE)
    }
}

class EndNode : ImageNode() {
    @FXML
    var saveButton: Button? = null

    override fun getValue(): Mat? {
        return nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return null
    }

    override fun addInit() {
        rootPane!!.onDragDetected = null

        nodes[Link.ONE] = Triple(firstLink!!, null, NodeTypes.IMAGE)

        (firstLink!!.children.find { it is Label } as Label).text = Titles.IMAGE

        saveButton!!.onAction = EventHandler {
            val mat = nodes[Link.ONE]!!.second?.getValue() as Mat? ?: return@EventHandler

            val fileChooser = FileChooser()
            fileChooser.title = Titles.SAVE_IMAGE
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter(
                Titles.IMAGE_FILES, Formats.PNG, Formats.JPG)
            )
            val dir = fileChooser.showSaveDialog(scene.window)
            if (dir != null) {
                try {
                    Imgcodecs.imwrite(dir.absolutePath, mat)
                } catch (e: IOException) {
                    println(e)
                }
            }
        }
    }

    override fun updateNode() {
        isRightNodes()
        val v = getValue()
        if (v != null) {
            imageView!!.isVisible = true
            imageView!!.image = Config.matToImage(v)
            saveButton!!.textFill = Paint.valueOf(Colors.BLACK)
        } else {
            saveButton!!.textFill = Paint.valueOf(Colors.RED)
        }
    }

    init {
        init(UIFXML.FINISH_NODE)
    }
}

class ImageNodeClass : ImageNode() {
    override val nodeType: NodeTypes = NodeTypes.IMAGE

    @FXML
    var openButton: Button? = null

    private var imageMat: Mat? = null
    private var path: String? = null

    override fun getValue(): Mat? {
        return imageMat
    }

    private fun getImage() {
        imageMat = Imgcodecs.imread(path)
        updateNode()
        imageView!!.isVisible = true
        outputLink?.kickAction()
    }

    override fun addInit() {
        openButton!!.onAction = EventHandler {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(
                FileChooser.ExtensionFilter(
                    Titles.IMAGE_FILES, Formats.PNG, Formats.JPG)
            )
            fileChooser.title = Titles.OPEN_IMAGE_FILE
            val file = fileChooser.showOpenDialog(scene.window)
            if (file != null) {
                path = file.absolutePath
                getImage()
            }
        }
    }

    override fun toData(): NodeData {
        val data = super.toData()
        data.data = path
        return data
    }

    override fun fromData(nodeData: NodeData) {
        super.fromData(nodeData)
        path = nodeData.data
        getImage()
    }

    init {
        init(UIFXML.IMAGE_NODE)
    }
}