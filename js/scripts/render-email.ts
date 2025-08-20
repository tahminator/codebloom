#!/usr/bin/env tsx

import { EmailRenderer } from '../src/emails/EmailRenderer';

interface EmailRequest {
  template: string;
  data: any;
}

async function renderEmail() {
  try {
    // Read JSON input from stdin or command line args
    const input = process.argv[2] || await readStdin();
    const request: EmailRequest = JSON.parse(input);
    
    let html: string;
    
    switch (request.template) {
      case 'school-verification':
        html = await EmailRenderer.renderSchoolVerificationEmail(request.data);
        break;
      default:
        throw new Error(`Unknown template: ${request.template}`);
    }
    
    console.log(html);
  } catch (error) {
    console.error('Error rendering email:', (error as Error).message);
    process.exit(1);
  }
}

function readStdin(): Promise<string> {
  return new Promise((resolve, reject) => {
    let input = '';
    process.stdin.setEncoding('utf8');
    
    process.stdin.on('data', (chunk) => {
      input += chunk;
    });
    
    process.stdin.on('end', () => {
      resolve(input.trim());
    });
    
    process.stdin.on('error', reject);
  });
}

renderEmail();