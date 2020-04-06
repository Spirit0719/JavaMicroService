package cn.zhiu.framework.base.api.core.annotation.request;

import com.squareup.javapoet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;



public class FeignRequestApiProxyProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        System.err.println("FeignRequestApiProxyProcessor Run");
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.err.println("FeignRequestApiProxyProcessor Process");

        for (Element em : roundEnv.getElementsAnnotatedWith(FeignRequestApiProxy.class)) {
            FeignClient feignClientAnnotation = em.getAnnotation(FeignClient.class);

            if (!em.getKind().equals(ElementKind.INTERFACE)) {
                error(em, "错误的注解类型, 只有接口对象能够被该 @%s 注解处理", FeignRequestApiProxy.class.getSimpleName());
                return true;
            }
            if (Objects.isNull(feignClientAnnotation)) {
                error(em, "错误的使用方式, 使用@%s的前提是当前接口包含@FeignClient注解", FeignRequestApiProxy.class.getSimpleName());
                return true;
            }

            TypeElement typeElement = (TypeElement) em;
            PackageElement packageElement = elementUtils.getPackageOf(typeElement);
            String name = typeElement.getSimpleName().toString();

            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(String.format("Feign%sProxy", name)).addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            typeSpecBuilder.addAnnotation(RestController.class);

            String capitalizeName = StringUtils.capitalize(name);
            FieldSpec.Builder builder = FieldSpec.builder(ClassName.get(typeElement), capitalizeName, Modifier.PRIVATE);
            builder.addAnnotation(Autowired.class);
            typeSpecBuilder.addField(builder.build());

            for (Element element : typeElement.getEnclosedElements()) {
                if (ElementKind.METHOD.equals(element.getKind())) {
                    ExecutableElement oldMethodElement = (ExecutableElement) element;
                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(oldMethodElement.getSimpleName().toString());
                    methodBuilder.addModifiers(Modifier.PROTECTED);
                    methodBuilder.returns(TypeName.get(oldMethodElement.getReturnType()));
                    for (AnnotationMirror oldAnnotationMirror : oldMethodElement.getAnnotationMirrors()) {
                        methodBuilder.addAnnotation(AnnotationSpec.get(oldAnnotationMirror));
                    }
                    List<String> parameterNames = new ArrayList<>();
                    for (VariableElement oldParameter : oldMethodElement.getParameters()) {
                        ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(ParameterizedTypeName.get(oldParameter.asType()), oldParameter.getSimpleName().toString(),
                                oldParameter.getModifiers().toArray(new Modifier[]{}));
                        for (AnnotationMirror oldAnnotationMirror : oldParameter.getAnnotationMirrors()) {
                            parameterBuilder.addAnnotation(AnnotationSpec.get(oldAnnotationMirror));
                        }
                        methodBuilder.addParameter(parameterBuilder.build());
                        parameterNames.add(oldParameter.getSimpleName().toString());
                    }
                    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                    codeBlockBuilder.addStatement(String.format("return %s.%s(%s)", capitalizeName, oldMethodElement.getSimpleName().toString(), String.join(",", parameterNames)));
                    methodBuilder.addCode(codeBlockBuilder.build());
                    typeSpecBuilder.addMethod(methodBuilder.build());
                }
            }

            TypeSpec typeSpec = typeSpecBuilder.build();

            String packageName = packageElement.getQualifiedName().toString();

            packageName = packageName.replace("base.api", "base.proxy.api");

            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();

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
        supportedAnnotationTypes.add("cn.zhiu.framework.base.api.core.annotation.request.FeignRequestApiProxy");
        return supportedAnnotationTypes;
    }

    protected void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
