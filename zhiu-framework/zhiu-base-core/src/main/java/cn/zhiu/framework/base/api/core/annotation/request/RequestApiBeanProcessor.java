package cn.zhiu.framework.base.api.core.annotation.request;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 实际的RequestApiBean注解处理器
 */
public class RequestApiBeanProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        System.err.println("RequestApiBeanProcessor Run");
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.err.println("RequestApiBeanProcessor Process");

        for (Element em : roundEnv.getElementsAnnotatedWith(RequestApiBean.class)) {
            if (!em.getKind().equals(ElementKind.CLASS)) {
                error(em, "错误的注解类型, 只有类对象能够被该 @%s 注解处理", RequestApiBean.class.getSimpleName());
                return true;
            }

            TypeElement typeElement = (TypeElement)em;

            PackageElement packageElement = elementUtils.getPackageOf(typeElement);

            // 开始组装类描述
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder("Q" + typeElement.getSimpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            for (VariableElement variableElement : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
                if (variableElement.getModifiers().contains(Modifier.PRIVATE)
                        && !variableElement.getModifiers().contains(Modifier.FINAL)
                        && !variableElement.getModifiers().contains(Modifier.STATIC)) {

                    String fieldName = variableElement.getSimpleName().toString();

                    // 只生成private的非final且非static修饰的属性
                    typeSpecBuilder.addField(FieldSpec.builder(String.class,
                            fieldName,
                            Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .initializer("$S", fieldName)
                            .build());
                }
            }

            TypeSpec typeSpec = typeSpecBuilder.build();

            JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), typeSpec).build();

            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new LinkedHashSet<>();
        supportedAnnotationTypes.add("cn.zhiu.framework.base.api.core.annotation.request.RequestApiBean");
        return supportedAnnotationTypes;
    }

    protected void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
