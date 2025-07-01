package io.github.nomisrev.typedapi.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.*
import com.google.devtools.ksp.getAnnotationsByType

val Endpoint = ClassName("io.github.nomisrev.typedapi", "Endpoint")
val EndpointAPI = ClassName("io.github.nomisrev.typedapi", "EndpointAPI")
val Request = ClassName("io.github.nomisrev.typedapi", "Request")

class EndpointProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Endpoint.canonicalName, inDepth = true)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .toList()

        if (symbols.isEmpty()) return emptyList()

        symbols.forEach { classDeclaration ->
            processEndpointClass(classDeclaration)
        }

        return symbols.filterNot { it.validate() }
    }

    private fun processEndpointClass(classDeclaration: KSClassDeclaration) {
        val className = classDeclaration.simpleName.asString()
        val packageName = classDeclaration.packageName.asString()

        // Extract path from the Endpoint annotation
        val path = classDeclaration.annotations
            .filter { it.shortName.asString() == "Endpoint" }
            .firstOrNull()
            ?.arguments
            ?.find { it.name?.asString() == "path" }
            ?.value as? String ?: ""

        // Get all properties from the class
        val properties = classDeclaration.getAllProperties()
            .filter { it.extensionReceiver == null }
            .toList()

        // Generate the data class name (remove "Api" suffix if present)
        val dataClassName = if (className.endsWith("Api", ignoreCase = true)) {
            className.substring(0, className.length - 3)
        } else {
            "${className}Data"
        }

        // Create the data class
        val dataClassBuilder = TypeSpec.classBuilder(dataClassName)
            .addModifiers(KModifier.DATA)
            .addAnnotation(ClassName("kotlinx.serialization", "Serializable"))

        // Add primary constructor with properties
        val constructorBuilder = FunSpec.constructorBuilder()

        // Add properties to data class
        val propertySpecs = mutableListOf<PropertySpec>()

        properties.forEach { property ->
            val propertyName = property.simpleName.asString()
            val propertyType = property.type.resolve()
            val typeName = propertyType.toTypeName()

            // Add property to constructor
            constructorBuilder.addParameter(propertyName, typeName)

            // Add property to class
            propertySpecs.add(
                PropertySpec.builder(propertyName, typeName)
                    .initializer(propertyName)
                    .build()
            )
        }

        dataClassBuilder.primaryConstructor(constructorBuilder.build())
        propertySpecs.forEach { dataClassBuilder.addProperty(it) }

        // Add request() function
        val requestFunctionBuilder = FunSpec.builder("request")
            .returns(
                ClassName("io.github.nomisrev.typedapi", "Request")
                    .parameterizedBy(ClassName(packageName, dataClassName), ClassName(packageName, className))
            )
            .addCode(
                """
                return %T(
                    this,
                    ::%L,
                    %LProperties,
                    %S
                )
                """.trimIndent(),
                ClassName("io.github.nomisrev.typedapi", "Request"),
                className,
                dataClassName.replaceFirstChar { it.lowercase(Locale.getDefault()) },
                path
            )

        dataClassBuilder.addFunction(requestFunctionBuilder.build())


        // Create the properties map
        val propertiesMapName = "${dataClassName.replaceFirstChar { it.lowercase(Locale.getDefault()) }}Properties"
        val propertiesMapBuilder = PropertySpec.builder(
            propertiesMapName,
            ClassName("kotlin.collections", "Map")
                .parameterizedBy(
                    ClassName("kotlin", "String"),
                    ClassName("kotlin.reflect", "KProperty1")
                        .parameterizedBy(ClassName(packageName, dataClassName), STAR)
                )
        )
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                buildString {
                    append("properties(\n")
                    properties.forEachIndexed { index, property ->
                        val propertyName = property.simpleName.asString()
                        append("    $dataClassName::$propertyName")
                        if (index < properties.size - 1) {
                            append(",\n")
                        } else {
                            append("\n")
                        }
                    }
                    append(")")
                }
            )

        // Create the file
        val fileSpec = FileSpec.builder(packageName, "${dataClassName}Generated")
            .addImport("io.github.nomisrev.typedapi", "properties")
            .addImport("io.github.nomisrev.typedapi", "Request")
            .addType(dataClassBuilder.build())
            .addProperty(propertiesMapBuilder.build())
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }
}

class EndpointProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EndpointProcessor(environment.codeGenerator, environment.logger)
    }
}