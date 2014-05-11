package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

// TODO: Not sure if create 2 different Translators.
// One responsible for constructors and another one for method calls
public class TracerTranslator implements Translator {

	// TODO: Maybe store templates in different file
	private final String constructorTemplate = 
			"{" +
					"	$_ = $proceed($$); " +
					"	ist.meic.pa.ObjectTracer.validateAndAddObject(($w)$_, $args, \"%s\", \"%s\", \"%s\");" +
					"}";

	// TODO: Maybe use addMethod here
	private final String methodTemplate = 
			"{" + 
					"	ist.meic.pa.ObjectTracer.addArgumentsMethod($args, \"%s\", \"%s\", \"%s\");" +
					"	$_ = $proceed($$); " +
					"	ist.meic.pa.ObjectTracer.addReturnMethod($_, \"%s\", \"%s\", \"%s\");" +
					"}";

	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
	CannotCompileException {
		if(TraceVM.isClassValid(className))  {
			CtClass ctClass = pool.get(className);
			TraceVM.addClientClass(className);
			Trace(ctClass);
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
	CannotCompileException {
		// TODO Auto-generated method stub
	}

	public void Trace(CtClass ctClass) throws CannotCompileException, NotFoundException {
		for(final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			NewExprTracer(ctMethod);
			FieldAccessTracer(ctMethod);
			MethodCallTracer(ctMethod);
		}
	}	

	// For new expressions
	public void NewExprTracer(CtMethod ctMethod) throws CannotCompileException {
		ctMethod.instrument(new ExprEditor() {
			public void edit(NewExpr newExpr) 
					throws CannotCompileException {
				try {
					newExpr.replace(String.format(
							constructorTemplate,
							newExpr.getConstructor().getLongName(),
							newExpr.getLineNumber(), 
							newExpr.getFileName()));
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void FieldAccessTracer(CtMethod ctMethod) throws CannotCompileException {
		ctMethod.instrument(new ExprEditor() {
			public void edit(FieldAccess fieldAccess) 
					throws CannotCompileException {
				try {
					if(!fieldAccess.getField().getType().isPrimitive() &&
					  (fieldAccess.isWriter() || fieldAccess.isReader())) {
						CtConstructor constructor = null; //Should never be null
						for(CtConstructor cc : fieldAccess.getField().getType().getConstructors()) {
							if(cc.getParameterTypes().length == 1) {
								constructor = cc;
								break;
							}
						}
						fieldAccess.replace(String.format(
								constructorTemplate,								
								constructor.getLongName(),
								fieldAccess.getLineNumber(), 
								fieldAccess.getFileName()));
					}
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// TODO: No enunciado: The arrow -> indicates that the object was provided as argument to
	// a constructor or method call
	// ConstructorCall
	public void MethodCallTracer(final CtMethod ctMethod) throws CannotCompileException {		
		ctMethod.instrument(new ExprEditor() {
			public void edit(MethodCall methodCall) 
					throws CannotCompileException {
				if(TraceVM.isClientClass(methodCall.getClassName())) {
					try {
						// TODO: get method name with enclosing class
						methodCall.replace(String.format(methodTemplate,
								methodCall.getMethod().getLongName(),
								methodCall.getLineNumber(), 
								methodCall.getFileName(),
								methodCall.getMethod().getLongName(),
								methodCall.getLineNumber(), 
								methodCall.getFileName()));
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
