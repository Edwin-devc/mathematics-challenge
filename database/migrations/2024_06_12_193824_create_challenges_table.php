<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::create('challenges', function (Blueprint $table) {
            $table->id('challenge_id')->primary();
            $table->string('title')->unique();
            $table->date('start_date');
            $table->date('end_date');
            $table->unsignedInteger('duration');
            $table->unsignedInteger('number_of_questions');
            $table->string('is_valid')->default("false");
            $table->timestamps();
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::dropIfExists('challenges');
        
        Schema::enableForeignKeyConstraints();
    }
};
