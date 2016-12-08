
import javax.sound.sampled.AudioFormat;
import javax.naming.*;
import javax.swing.UIManager;
import javax.swing.text.html.parser.*;

/**
 * Creates named and anonymous inner classes that refer to types in various ways
 */
class InnerBound {
	public void recombobulate() {
		
		
		// javax.sound.sampled.AudioFormat
		class InnerNamedExtendsImport extends AudioFormat {
			public InnerNamedExtendsImport() {
				super(0, 0, 0, false, false);
			}
		}
		// javax.naming.AuthenticationException
		class InnerNamedExtendsGlob extends AuthenticationException {
			
		}
		// javax.swing.text.ZoneVIew
		class InnerExtendsFullyQualified extends javax.swing.text.ZoneView {
			public InnerExtendsFullyQualified() {
				super(null, 0);
			}
		}
		
		// javax.swing.UIManager
		UIManager anonymousExtendsImport = new UIManager() {
			
		};
		
		// javax.swing.text.html.parser.TagElement
		TagElement anonymousExtendsGlob = new TagElement(null) {
			
		};
		
		// java.util.zip.ZipOutputStream
		java.util.zip.ZipOutputStream anonymousExtendsFullyQualified = new java.util.zip.ZipOutputStream(null) {
			
		};
		
	}
}
