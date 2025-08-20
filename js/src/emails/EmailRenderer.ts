import { render } from '@react-email/render';
import SchoolVerificationEmail from './SchoolVerificationEmail';

export interface SchoolVerificationEmailData {
  verificationLink: string;
  recipientEmail: string;
}

export class EmailRenderer {
  static async renderSchoolVerificationEmail(data: SchoolVerificationEmailData): Promise<string> {
    return render(SchoolVerificationEmail(data));
  }
  
  // Add more template renderers here as needed
}

export default EmailRenderer;